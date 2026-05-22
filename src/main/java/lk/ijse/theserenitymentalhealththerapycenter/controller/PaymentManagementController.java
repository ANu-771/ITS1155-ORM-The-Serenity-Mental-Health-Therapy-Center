package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PaymentBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.PaymentTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.PaymentException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentManagementController implements Initializable {

    @FXML private TextField txtPaymentId, txtAmount;
    @FXML private DatePicker dpDate;
    @FXML private ComboBox<String> cmbPatient, cmbProgram, cmbMethod, cmbStatus;
    @FXML private TableView<PaymentTM> tblPayments;

    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbMethod.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer", "Online"));
        cmbStatus.setItems(FXCollections.observableArrayList("PENDING", "COMPLETED", "FAILED"));
        txtPaymentId.setEditable(false);

        // Default to today's date
        dpDate.setValue(LocalDate.now());

        generateNextId();
        loadCombos();
        loadTable();

        tblPayments.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtPaymentId.setText(nw.getPaymentId());
                txtAmount.setText(String.valueOf(nw.getAmount()));
                // Parse date string back to LocalDate
                try {
                    dpDate.setValue(nw.getPaymentDate() != null && !nw.getPaymentDate().isEmpty()
                            ? LocalDate.parse(nw.getPaymentDate()) : null);
                } catch (Exception ignored) { dpDate.setValue(null); }
                cmbMethod.setValue(nw.getPaymentMethod());
                cmbStatus.setValue(nw.getStatus());
                ValidationUtil.resetStyles(txtPaymentId, txtAmount);
            }
        });
    }

    private void loadCombos() {
        try {
            List<PatientDTO> patients = patientBO.getAllPatients();
            ObservableList<String> pList = FXCollections.observableArrayList();
            for (PatientDTO p : patients) pList.add(p.getId() + " - " + p.getName());
            cmbPatient.setItems(pList);

            List<TherapyProgramDTO> programs = programBO.getAllPrograms();
            ObservableList<String> prList = FXCollections.observableArrayList();
            for (TherapyProgramDTO p : programs) prList.add(p.getProgramId() + " - " + p.getName());
            cmbProgram.setItems(prList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String extractId(String comboVal) {
        return comboVal != null ? comboVal.split(" - ")[0] : null;
    }

    private void generateNextId() {
        try { txtPaymentId.setText(paymentBO.getNextId()); } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleSave(ActionEvent e) {
        ValidationUtil.resetStyles(txtPaymentId, txtAmount);

        // Required fields
        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtAmount)) allFilled = false;
        if (dpDate.getValue() == null) {
            dpDate.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            allFilled = false;
        }
        if (!ValidationUtil.validateRequired(cmbPatient)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbProgram)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbMethod)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbStatus)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String dateStr = dpDate.getValue().toString();
            PaymentDTO dto = new PaymentDTO(
                    txtPaymentId.getText().trim(), amount, dateStr,
                    cmbMethod.getValue(), cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbProgram.getValue()), null);
            paymentBO.savePayment(dto);
            new Alert(Alert.AlertType.INFORMATION, "Payment saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
            generateNextId();
        } catch (NumberFormatException ex) {
            ValidationUtil.setInvalid(txtAmount);
            new Alert(Alert.AlertType.WARNING, "Invalid amount. Please enter a valid number.").showAndWait();
        } catch (PaymentException | InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        ValidationUtil.resetStyles(txtPaymentId, txtAmount);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtPaymentId)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtAmount)) allFilled = false;
        if (dpDate.getValue() == null) {
            dpDate.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            allFilled = false;
        }

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            double amount = Double.parseDouble(txtAmount.getText().trim());
            String dateStr = dpDate.getValue().toString();
            PaymentDTO dto = new PaymentDTO(
                    txtPaymentId.getText().trim(), amount, dateStr,
                    cmbMethod.getValue(), cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbProgram.getValue()), null);
            paymentBO.updatePayment(dto);
            new Alert(Alert.AlertType.INFORMATION, "Payment updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (NumberFormatException ex) {
            ValidationUtil.setInvalid(txtAmount);
            new Alert(Alert.AlertType.WARNING, "Invalid amount.").showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtPaymentId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a payment to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) return;

        try {
            paymentBO.deletePayment(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Payment deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtPaymentId.clear(); txtAmount.clear();
        dpDate.setValue(LocalDate.now());
        dpDate.setStyle("");
        cmbPatient.setValue(null); cmbProgram.setValue(null);
        cmbMethod.setValue(null); cmbStatus.setValue(null);
        ValidationUtil.resetStyles(txtPaymentId, txtAmount);
        generateNextId();
    }

    private void loadTable() {
        try {
            List<PaymentDTO> all = paymentBO.getAllPayments();
            ObservableList<PaymentTM> list = FXCollections.observableArrayList();
            for (PaymentDTO p : all) {
                list.add(new PaymentTM(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getPatientName(), p.getProgramName()));
            }
            tblPayments.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
