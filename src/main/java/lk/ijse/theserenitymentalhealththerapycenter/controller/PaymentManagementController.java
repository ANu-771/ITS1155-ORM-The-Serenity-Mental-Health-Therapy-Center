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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentManagementController implements Initializable {

    @FXML private TextField txtPaymentId, txtAmount, txtDate;
    @FXML private ComboBox<String> cmbPatient, cmbProgram, cmbMethod, cmbStatus;
    @FXML private TableView<PaymentTM> tblPayments;

    private final PaymentBO paymentBO = BOFactory.getInstance().getBO(BOFactory.BOType.PAYMENT);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbMethod.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer", "Online"));
        cmbStatus.setItems(FXCollections.observableArrayList("PENDING", "COMPLETED", "FAILED"));
        loadCombos();
        loadTable();
        tblPayments.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) { txtPaymentId.setText(nw.getPaymentId()); txtAmount.setText(String.valueOf(nw.getAmount())); txtDate.setText(nw.getPaymentDate()); cmbMethod.setValue(nw.getPaymentMethod()); cmbStatus.setValue(nw.getStatus()); }
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

    private String extractId(String comboVal) { return comboVal != null ? comboVal.split(" - ")[0] : null; }

    @FXML void handleSave(ActionEvent e) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            PaymentDTO dto = new PaymentDTO(txtPaymentId.getText(), amount, txtDate.getText(), cmbMethod.getValue(), cmbStatus.getValue(), extractId(cmbPatient.getValue()), null, extractId(cmbProgram.getValue()), null);
            paymentBO.savePayment(dto);
            new Alert(Alert.AlertType.INFORMATION, "Payment saved!").showAndWait(); loadTable(); handleClear(null);
        } catch (NumberFormatException ex) { new Alert(Alert.AlertType.WARNING, "Invalid amount").showAndWait();
        } catch (PaymentException | InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleUpdate(ActionEvent e) {
        try {
            double amount = Double.parseDouble(txtAmount.getText());
            PaymentDTO dto = new PaymentDTO(txtPaymentId.getText(), amount, txtDate.getText(), cmbMethod.getValue(), cmbStatus.getValue(), extractId(cmbPatient.getValue()), null, extractId(cmbProgram.getValue()), null);
            paymentBO.updatePayment(dto);
            new Alert(Alert.AlertType.INFORMATION, "Payment updated!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleDelete(ActionEvent e) {
        try { paymentBO.deletePayment(txtPaymentId.getText()); new Alert(Alert.AlertType.INFORMATION, "Deleted!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
    }

    @FXML void handleClear(ActionEvent e) { txtPaymentId.clear(); txtAmount.clear(); txtDate.clear(); cmbPatient.setValue(null); cmbProgram.setValue(null); cmbMethod.setValue(null); cmbStatus.setValue(null); }

    private void loadTable() {
        try {
            List<PaymentDTO> all = paymentBO.getAllPayments();
            ObservableList<PaymentTM> list = FXCollections.observableArrayList();
            for (PaymentDTO p : all) list.add(new PaymentTM(p.getPaymentId(), p.getAmount(), p.getPaymentDate(), p.getPaymentMethod(), p.getStatus(), p.getPatientName(), p.getProgramName()));
            tblPayments.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
