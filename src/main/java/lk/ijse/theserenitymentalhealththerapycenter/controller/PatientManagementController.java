package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.PatientDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.RegistrationInvoiceDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.PatientTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class PatientManagementController implements Initializable {

    @FXML
    private TextField txtId, txtName, txtContact, txtSearch;
    @FXML
    private DatePicker dpDob, dpRegDate;
    @FXML
    private TextArea txtMedicalHistory;
    @FXML
    private TableView<PatientTM> tblPatients;
    @FXML
    private ComboBox<String> cmbGender, cmbPaymentMethod;
    @FXML
    private CheckBox chkVerified;
    @FXML
    private VBox vboxPrograms;
    @FXML
    private TextField txtTotalFee, txtUpfrontPayment, txtDueBalance;

    private List<lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO> allPrograms;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setEditable(false);
        generateNextId();
        loadTable();


        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female"));
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer", "Online"));
        
        loadPrograms();

        txtUpfrontPayment.textProperty().addListener((obs, old, nw) -> calculateDueBalance());

        dpRegDate.setValue(LocalDate.now());
        dpRegDate.setEditable(false);
        dpRegDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });

        dpDob.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isAfter(LocalDate.now()));
            }
        });

        tblPatients.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtId.setText(nw.getId());
                txtName.setText(nw.getName());
                dpDob.setValue(nw.getDob() != null && !nw.getDob().isEmpty() ? LocalDate.parse(nw.getDob()) : null);
                txtContact.setText(nw.getContactNumber());
                cmbGender.setValue(nw.getGender());
                dpRegDate.setValue(nw.getRegistrationDate() != null && !nw.getRegistrationDate().isEmpty() ? LocalDate.parse(nw.getRegistrationDate()) : null);

                vboxPrograms.setDisable(true);
                chkVerified.setDisable(true);
                txtUpfrontPayment.setDisable(true);
                cmbPaymentMethod.setDisable(true);

                resetValidationStyles();
            }
        });
    }

    private void loadPrograms() {
        try {
            allPrograms = programBO.getAllPrograms();
            vboxPrograms.getChildren().clear();
            for (lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO p : allPrograms) {
                CheckBox chk = new CheckBox(p.getProgramId() + " - " + p.getName() + " (LKR " + p.getFee() + ")");
                chk.getStyleClass().add("form-label");
                chk.setUserData(p);
                chk.selectedProperty().addListener((obs, oldVal, newVal) -> calculateTotalFee());
                vboxPrograms.getChildren().add(chk);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateTotalFee() {
        double total = 0;
        for (javafx.scene.Node node : vboxPrograms.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox chk = (CheckBox) node;
                if (chk.isSelected()) {
                    lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO p = (lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO) chk.getUserData();
                    total += p.getFee();
                }
            }
        }
        txtTotalFee.setText(String.valueOf(total));
        calculateDueBalance();
    }

    private void calculateDueBalance() {
        try {
            double total = txtTotalFee.getText().isEmpty() ? 0 : Double.parseDouble(txtTotalFee.getText());
            double paid = txtUpfrontPayment.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtUpfrontPayment.getText().trim());
            txtDueBalance.setText(String.valueOf(total - paid));
        } catch (NumberFormatException e) {
            txtDueBalance.setText("Invalid Input");
        }
    }

    private void generateNextId() {
        try {
            txtId.setText(patientBO.getNextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetValidationStyles() {
        ValidationUtil.resetStyles(txtId, txtName, txtContact);
        dpDob.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        dpRegDate.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
    }

    private boolean validateDatePicker(DatePicker picker) {
        if (picker.getValue() == null) {
            picker.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            return false;
        }
        picker.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        return true;
    }


    private boolean validateFields(boolean isSave) {
        resetValidationStyles();

        if (txtName.getText() == null || txtName.getText().trim().isEmpty()) {
            txtName.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Full Name is required.").showAndWait();
            txtName.requestFocus();
            return false;
        }

        if (!txtName.getText().trim().matches("^[A-Za-z\\s\\.-]+$")) {
            txtName.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Invalid Name. The patient's name can only contain letters, spaces, hyphens, and periods.").showAndWait();
            txtName.requestFocus();
            return false;
        }

        if (txtContact.getText() == null || txtContact.getText().trim().isEmpty()) {
            txtContact.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Contact Number is required.").showAndWait();
            txtContact.requestFocus();
            return false;
        }

        if (!txtContact.getText().trim().matches("^\\d{10}$")) {
            txtContact.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Invalid Contact Number. The mobile number must be exactly 10 digits long.").showAndWait();
            txtContact.requestFocus();
            return false;
        }

        if (cmbGender.getValue() == null) {
            cmbGender.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Please select Gender.").showAndWait();
            cmbGender.requestFocus();
            return false;
        }

        if (dpRegDate.getValue() == null) {
            dpRegDate.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8;");
            new Alert(Alert.AlertType.WARNING, "Registration Date is required.").showAndWait();
            dpRegDate.requestFocus();
            return false;
        }

        if (isSave) {
            if (!chkVerified.isSelected()) {
                new Alert(Alert.AlertType.WARNING, "Registration blocked: Interview must be passed and documents verified.").showAndWait();
                chkVerified.requestFocus();
                return false;
            }

            boolean anyProgramSelected = false;
            for (javafx.scene.Node node : vboxPrograms.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    anyProgramSelected = true;
                    break;
                }
            }
            if (!anyProgramSelected) {
                new Alert(Alert.AlertType.WARNING, "At least one Therapy Program is required for registration.").showAndWait();
                return false;
            }

            if (txtUpfrontPayment.getText().trim().isEmpty()) {
                ValidationUtil.setInvalid(txtUpfrontPayment);
                new Alert(Alert.AlertType.WARNING, "Upfront Payment is required.").showAndWait();
                return false;
            }

            try {
                Double.parseDouble(txtUpfrontPayment.getText().trim());
            } catch (NumberFormatException e) {
                ValidationUtil.setInvalid(txtUpfrontPayment);
                new Alert(Alert.AlertType.WARNING, "Invalid Upfront Payment amount.").showAndWait();
                return false;
            }

            if (cmbPaymentMethod.getValue() == null) {
                ValidationUtil.setInvalid(cmbPaymentMethod);
                new Alert(Alert.AlertType.WARNING, "Payment Method is required.").showAndWait();
                return false;
            }
        }

        return true;
    }

    @FXML
    void handleSave(ActionEvent e) {
        if (!validateFields(true)) return;

        try {
            String dobStr = dpDob.getValue() != null ? dpDob.getValue().toString() : "";
            String regDateStr = dpRegDate.getValue() != null ? dpRegDate.getValue().toString() : "";

            PatientDTO dto = new PatientDTO(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    dobStr,
                    txtContact.getText().trim(),
                    cmbGender.getValue(),
                    txtMedicalHistory.getText(),
                    regDateStr,
                    chkVerified.isSelected()
            );

            List<String> programIds = new java.util.ArrayList<>();
            for (javafx.scene.Node node : vboxPrograms.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO p = (lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO) ((CheckBox) node).getUserData();
                    programIds.add(p.getProgramId());
                }
            }

            double upfrontPayment = Double.parseDouble(txtUpfrontPayment.getText().trim());
            String paymentMethod = cmbPaymentMethod.getValue();

            patientBO.registerPatient(dto, programIds, upfrontPayment, paymentMethod);
            
            // Calculate variables for JasperReport
            List<RegistrationInvoiceDTO> invoiceItems = new java.util.ArrayList<>();
            double remainingPayment = upfrontPayment;
            double totalFee = 0;

            for (javafx.scene.Node node : vboxPrograms.getChildren()) {
                if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
                    lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO p = (lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO) ((CheckBox) node).getUserData();
                    totalFee += p.getFee();
                    
                    double appliedAmount = 0.0;
                    if (remainingPayment >= p.getFee()) {
                        appliedAmount = p.getFee();
                        remainingPayment -= p.getFee();
                    } else if (remainingPayment > 0) {
                        appliedAmount = remainingPayment;
                        remainingPayment = 0;
                    }

                    int coveredSessions = 0;
                    if (p.getTotalSessions() > 0) {
                        double perSessionRate = p.getFee() / p.getTotalSessions();
                        coveredSessions = (int) Math.floor(appliedAmount / perSessionRate);
                    }
                    
                    invoiceItems.add(new RegistrationInvoiceDTO(p.getName(), p.getFee(), p.getTotalSessions(), coveredSessions));
                }
            }

            try {
                InputStream is = this.getClass().getResourceAsStream("/reports/RegistrationInvoice.jrxml");
                if (is != null) {
                    JasperReport jr = JasperCompileManager.compileReport(is);
                    java.util.Map<String, Object> params = new java.util.HashMap<>();
                    params.put("PatientId", dto.getId());
                    params.put("PatientName", dto.getName());
                    params.put("TotalFee", totalFee);
                    params.put("UpfrontPayment", upfrontPayment);
                    params.put("DueBalance", totalFee - upfrontPayment);

                    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(invoiceItems);
                    JasperPrint jp = JasperFillManager.fillReport(jr, params, dataSource);
                    JasperViewer.viewReport(jp, false);
                }
            } catch (Exception jasperEx) {
                new Alert(Alert.AlertType.ERROR, "Failed to generate invoice: " + jasperEx.getMessage()).showAndWait();
                jasperEx.printStackTrace();
            }

            new Alert(Alert.AlertType.INFORMATION, "Patient registered successfully!").showAndWait();
            
            loadTable();
            handleClear(null);
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
            ex.printStackTrace();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        if (!validateFields(false)) return;

        try {
            String dobStr = dpDob.getValue() != null ? dpDob.getValue().toString() : "";
            String regDateStr = dpRegDate.getValue() != null ? dpRegDate.getValue().toString() : "";

            patientBO.updatePatient(new PatientDTO(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    dobStr,
                    txtContact.getText().trim(),
                    cmbGender.getValue(),
                    txtMedicalHistory.getText(),
                    regDateStr,
                    chkVerified.isSelected()
            ));
            new Alert(Alert.AlertType.INFORMATION, "Patient updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) {
            return;
        }

        try {
            patientBO.deletePatient(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtName.clear();
        dpDob.setValue(null);
        txtContact.clear();
        cmbGender.setValue(null);
        txtMedicalHistory.clear();
        dpRegDate.setValue(LocalDate.now());

        vboxPrograms.setDisable(false);
        for (javafx.scene.Node node : vboxPrograms.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
        chkVerified.setDisable(false);
        chkVerified.setSelected(false);
        txtTotalFee.clear();
        txtUpfrontPayment.setDisable(false);
        txtUpfrontPayment.clear();
        txtDueBalance.clear();
        cmbPaymentMethod.setDisable(false);
        cmbPaymentMethod.setValue(null);

        if (txtSearch != null) txtSearch.clear();
        resetValidationStyles();
        generateNextId();
    }

    @FXML
    void handleSearch(javafx.scene.input.KeyEvent e) {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) {
            loadTable();
            return;
        }
        try {
            List<PatientDTO> results = patientBO.searchPatientsByName(q);
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : results) {
                list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getRegistrationDate()));
            }
            tblPatients.setItems(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadTable() {
        try {
            List<PatientDTO> all = patientBO.getAllPatients();
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : all) {
                list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getRegistrationDate()));
            }
            tblPatients.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
