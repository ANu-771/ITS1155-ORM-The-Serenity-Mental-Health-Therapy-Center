package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.PatientDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.PatientTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientManagementController implements Initializable {

    @FXML private TextField txtId, txtName, txtDob, txtContact, txtEmail, txtRegDate, txtSearch;
    @FXML private TextArea txtMedicalHistory;
    @FXML private TableView<PatientTM> tblPatients;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setEditable(false);
        generateNextId();
        loadTable();
        tblPatients.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtId.setText(nw.getId());
                txtName.setText(nw.getName());
                txtDob.setText(nw.getDob());
                txtContact.setText(nw.getContactNumber());
                txtEmail.setText(nw.getEmail());
                txtRegDate.setText(nw.getRegistrationDate());
                ValidationUtil.resetStyles(txtId, txtName, txtDob, txtContact, txtEmail, txtRegDate);
            }
        });
    }

    private void generateNextId() {
        try { txtId.setText(patientBO.getNextId()); } catch (Exception e) { e.printStackTrace(); }
    }

    // ===== SAVE — with required fields + regex validation =====
    @FXML
    void handleSave(ActionEvent e) {
        // Reset all borders first
        ValidationUtil.resetStyles(txtId, txtName, txtDob, txtContact, txtEmail, txtRegDate);

        // 1. Required fields check
        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtContact)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtRegDate)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        // 2. Regex validation
        boolean valid = true;

        if (!ValidationUtil.validateName(txtName)) {
            valid = false;
        }
        if (!ValidationUtil.validatePhone(txtContact)) {
            valid = false;
        }
        // Email is optional, but validate format if provided
        if (txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty()) {
            if (!ValidationUtil.validateEmail(txtEmail)) {
                valid = false;
            }
        }

        if (!valid) {
            new Alert(Alert.AlertType.WARNING, "Please correct the highlighted fields.").showAndWait();
            return;
        }

        // 3. Save via BO layer
        try {
            patientBO.savePatient(new PatientDTO(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    txtDob.getText().trim(),
                    txtContact.getText().trim(),
                    txtEmail.getText().trim(),
                    txtMedicalHistory.getText(),
                    txtRegDate.getText().trim()
            ));
            new Alert(Alert.AlertType.INFORMATION, "Patient saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    // ===== UPDATE — same validation pattern =====
    @FXML
    void handleUpdate(ActionEvent e) {
        ValidationUtil.resetStyles(txtId, txtName, txtDob, txtContact, txtEmail, txtRegDate);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtId)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtContact)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        boolean valid = true;
        if (!ValidationUtil.validateName(txtName)) valid = false;
        if (!ValidationUtil.validatePhone(txtContact)) valid = false;
        if (txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty()) {
            if (!ValidationUtil.validateEmail(txtEmail)) valid = false;
        }

        if (!valid) {
            new Alert(Alert.AlertType.WARNING, "Please correct the highlighted fields.").showAndWait();
            return;
        }

        try {
            patientBO.updatePatient(new PatientDTO(
                    txtId.getText().trim(),
                    txtName.getText().trim(),
                    txtDob.getText().trim(),
                    txtContact.getText().trim(),
                    txtEmail.getText().trim(),
                    txtMedicalHistory.getText(),
                    txtRegDate.getText().trim()
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

    // ===== DELETE — with confirmation dialog =====
    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient to delete.").showAndWait();
            return;
        }

        // Show confirmation dialog before deleting
        if (!ValidationUtil.confirmDelete()) {
            return; // User cancelled
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

    // ===== CLEAR =====
    @FXML
    void handleClear(ActionEvent e) {
        txtName.clear(); txtDob.clear();
        txtContact.clear(); txtEmail.clear(); txtMedicalHistory.clear(); txtRegDate.clear();
        if (txtSearch != null) txtSearch.clear();
        ValidationUtil.resetStyles(txtId, txtName, txtDob, txtContact, txtEmail, txtRegDate);
        generateNextId();
    }

    // ===== SEARCH =====
    @FXML
    void handleSearch(javafx.scene.input.KeyEvent e) {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) { loadTable(); return; }
        try {
            List<PatientDTO> results = patientBO.searchPatientsByName(q);
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : results) {
                list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getRegistrationDate()));
            }
            tblPatients.setItems(list);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // ===== LOAD TABLE =====
    private void loadTable() {
        try {
            List<PatientDTO> all = patientBO.getAllPatients();
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : all) {
                list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getRegistrationDate()));
            }
            tblPatients.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
