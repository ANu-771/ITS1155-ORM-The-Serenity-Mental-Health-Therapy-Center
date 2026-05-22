package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapySessionBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapistBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.TherapySessionTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.SchedulingConflictException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SessionManagementController implements Initializable {

    @FXML
    private TextField txtSessionId;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<String> cmbTime, cmbStatus, cmbPatient, cmbTherapist, cmbProgram;
    @FXML
    private TableView<TherapySessionTM> tblSessions;

    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Status options
        cmbStatus.setItems(FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELLED"));

        // Time slots
        cmbTime.setItems(FXCollections.observableArrayList(
                "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
                "12:00 PM", "01:00 PM", "02:00 PM", "03:00 PM",
                "04:00 PM", "05:00 PM"
        ));

        dpDate.setDayCellFactory(getDisablePastDatesCellFactory());

        txtSessionId.setEditable(false);
        generateNextId();
        loadCombos();
        loadTable();

        tblSessions.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtSessionId.setText(nw.getSessionId());
                dpDate.setValue(LocalDate.parse(nw.getDate()));
                cmbTime.setValue(nw.getTime());
                cmbStatus.setValue(nw.getStatus());
            }
        });
    }


    private Callback<DatePicker, DateCell> getDisablePastDatesCellFactory() {
        return datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #e9ecef; -fx-text-fill: #adb5bd;");
                }
            }
        };
    }

    private void loadCombos() {
        try {
            // Patients
            List<PatientDTO> patients = patientBO.getAllPatients();
            ObservableList<String> pList = FXCollections.observableArrayList();
            for (PatientDTO p : patients) pList.add(p.getId() + " - " + p.getName());
            cmbPatient.setItems(pList);

            // Therapists
            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            ObservableList<String> tList = FXCollections.observableArrayList();
            for (TherapistDTO t : therapists) tList.add(t.getId() + " - " + t.getName());
            cmbTherapist.setItems(tList);

            List<TherapyProgramDTO> programs = programBO.getAllPrograms();
            ObservableList<String> prList = FXCollections.observableArrayList();
            for (TherapyProgramDTO p : programs) prList.add(p.getProgramId() + " - " + p.getName());
            cmbProgram.setItems(prList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractId(String comboVal) {
        return comboVal != null ? comboVal.split(" - ")[0] : null;
    }

    private void generateNextId() {
        try {
            txtSessionId.setText(sessionBO.getNextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent e) {
        boolean allFilled = true;
        if (dpDate.getValue() == null) {
            dpDate.setStyle("-fx-border-color: #e74c3c;");
            allFilled = false;
        }
        if (cmbTime.getValue() == null) {
            cmbTime.setStyle("-fx-border-color: #e74c3c;");
            allFilled = false;
        }
        if (!ValidationUtil.validateRequired(cmbPatient)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbTherapist)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbProgram)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbStatus)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            String dateStr = dpDate.getValue().toString();
            String timeStr = cmbTime.getValue();

            TherapySessionDTO dto = new TherapySessionDTO(
                    txtSessionId.getText().trim(), dateStr, timeStr, cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbTherapist.getValue()), null,
                    extractId(cmbProgram.getValue()), null);

            sessionBO.saveSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
            generateNextId();
        } catch (SchedulingConflictException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "This therapist is already booked for this time slot. Please select a different time or therapist.")
                    .showAndWait();
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtSessionId)) allFilled = false;
        if (dpDate.getValue() == null) {
            dpDate.setStyle("-fx-border-color: #e74c3c;");
            allFilled = false;
        }
        if (cmbTime.getValue() == null) {
            cmbTime.setStyle("-fx-border-color: #e74c3c;");
            allFilled = false;
        }

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            String dateStr = dpDate.getValue().toString();
            String timeStr = cmbTime.getValue();

            TherapySessionDTO dto = new TherapySessionDTO(
                    txtSessionId.getText().trim(), dateStr, timeStr, cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null,
                    extractId(cmbTherapist.getValue()), null,
                    extractId(cmbProgram.getValue()), null);

            sessionBO.updateSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (SchedulingConflictException ex) {
            new Alert(Alert.AlertType.ERROR,
                    "This therapist is already booked for this time slot. Please select a different time or therapist.")
                    .showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtSessionId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a session to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) return;

        try {
            sessionBO.deleteSession(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Session deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtSessionId.clear();
        dpDate.setValue(null);
        cmbTime.setValue(null);
        cmbStatus.setValue(null);
        cmbPatient.setValue(null);
        cmbTherapist.setValue(null);
        cmbProgram.setValue(null);
        dpDate.setStyle("");
        cmbTime.setStyle("");
        ValidationUtil.resetStyles(txtSessionId);
        generateNextId();
    }

    private void loadTable() {
        try {
            List<TherapySessionDTO> all = sessionBO.getAllSessions();
            ObservableList<TherapySessionTM> list = FXCollections.observableArrayList();
            for (TherapySessionDTO s : all) {
                list.add(new TherapySessionTM(s.getSessionId(), s.getDate(), s.getTime(), s.getStatus(), s.getPatientName(), s.getTherapistName(), s.getProgramName()));
            }
            tblSessions.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
