package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.PatientDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.PatientTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

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
    private ComboBox<String> cmbProgram, cmbGender;

    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setEditable(false);
        generateNextId();
        loadTable();


        cmbGender.setItems(FXCollections.observableArrayList("Male", "Female"));
        loadPrograms();

        // Automatically set Registration Date to today's date
        dpRegDate.setValue(LocalDate.now());

        // Disable future dates for DOB DatePicker
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

                cmbProgram.setDisable(true);
                cmbProgram.setValue(null);

                resetValidationStyles();
            }
        });
    }

    private void loadPrograms() {
        try {
            List<lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO> programs = programBO.getAllPrograms();
            ObservableList<String> prList = FXCollections.observableArrayList();
            for (lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO p : programs) {
                prList.add(p.getProgramId() + " - " + p.getName());
            }
            cmbProgram.setItems(prList);


        } catch (Exception e) {
            e.printStackTrace();
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


    @FXML
    void handleSave(ActionEvent e) {
        resetValidationStyles();


        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtContact)) allFilled = false;
        if (!validateDatePicker(dpRegDate)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbProgram)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbGender)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        boolean valid = true;
        if (!ValidationUtil.validateName(txtName)) {
            valid = false;
        }
        if (!ValidationUtil.validatePhone(txtContact)) {
            valid = false;
        }

        if (!valid) {
            new Alert(Alert.AlertType.WARNING, "Please correct the highlighted fields.").showAndWait();
            return;
        }

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
                    regDateStr
            );

            String programId = cmbProgram.getValue().split(" - ")[0];

            patientBO.registerPatient(dto, programId);
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
        resetValidationStyles();

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtId)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtContact)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbGender)) allFilled = false;
        if (!validateDatePicker(dpRegDate)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        boolean valid = true;
        if (!ValidationUtil.validateName(txtName)) valid = false;
        if (!ValidationUtil.validatePhone(txtContact)) valid = false;

        if (!valid) {
            new Alert(Alert.AlertType.WARNING, "Please correct the highlighted fields.").showAndWait();
            return;
        }

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
                    regDateStr
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

        cmbProgram.setDisable(false);
        cmbProgram.setValue(null);

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
