package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapySessionBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapistBO;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.*;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.TherapySessionTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.SchedulingConflictException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SessionManagementController implements Initializable {

    @FXML private TextField txtSessionId, txtDate, txtTime;
    @FXML private ComboBox<String> cmbStatus, cmbPatient, cmbTherapist, cmbProgram;
    @FXML private TableView<TherapySessionTM> tblSessions;

    private final TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
    private final PatientBO patientBO = BOFactory.getInstance().getBO(BOFactory.BOType.PATIENT);
    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);
    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cmbStatus.setItems(FXCollections.observableArrayList("SCHEDULED", "COMPLETED", "CANCELLED"));
        loadCombos();
        loadTable();
        tblSessions.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) { txtSessionId.setText(nw.getSessionId()); txtDate.setText(nw.getDate()); txtTime.setText(nw.getTime()); cmbStatus.setValue(nw.getStatus()); }
        });
    }

    private void loadCombos() {
        try {
            List<PatientDTO> patients = patientBO.getAllPatients();
            ObservableList<String> pList = FXCollections.observableArrayList();
            for (PatientDTO p : patients) pList.add(p.getId() + " - " + p.getName());
            cmbPatient.setItems(pList);

            List<TherapistDTO> therapists = therapistBO.getAllTherapists();
            ObservableList<String> tList = FXCollections.observableArrayList();
            for (TherapistDTO t : therapists) tList.add(t.getId() + " - " + t.getName());
            cmbTherapist.setItems(tList);

            List<TherapyProgramDTO> programs = programBO.getAllPrograms();
            ObservableList<String> prList = FXCollections.observableArrayList();
            for (TherapyProgramDTO p : programs) prList.add(p.getProgramId() + " - " + p.getName());
            cmbProgram.setItems(prList);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private String extractId(String comboVal) {
        return comboVal != null ? comboVal.split(" - ")[0] : null;
    }

    @FXML void handleSave(ActionEvent e) {
        try {
            TherapySessionDTO dto = new TherapySessionDTO(txtSessionId.getText(), txtDate.getText(), txtTime.getText(), cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null, extractId(cmbTherapist.getValue()), null, extractId(cmbProgram.getValue()), null);
            sessionBO.saveSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session saved!").showAndWait(); loadTable(); handleClear(null);
        } catch (SchedulingConflictException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleUpdate(ActionEvent e) {
        try {
            TherapySessionDTO dto = new TherapySessionDTO(txtSessionId.getText(), txtDate.getText(), txtTime.getText(), cmbStatus.getValue(),
                    extractId(cmbPatient.getValue()), null, extractId(cmbTherapist.getValue()), null, extractId(cmbProgram.getValue()), null);
            sessionBO.updateSession(dto);
            new Alert(Alert.AlertType.INFORMATION, "Session updated!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleDelete(ActionEvent e) {
        try { sessionBO.deleteSession(txtSessionId.getText()); new Alert(Alert.AlertType.INFORMATION, "Deleted!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
    }

    @FXML void handleClear(ActionEvent e) {
        txtSessionId.clear(); txtDate.clear(); txtTime.clear(); cmbStatus.setValue(null); cmbPatient.setValue(null); cmbTherapist.setValue(null); cmbProgram.setValue(null);
    }

    private void loadTable() {
        try {
            List<TherapySessionDTO> all = sessionBO.getAllSessions();
            ObservableList<TherapySessionTM> list = FXCollections.observableArrayList();
            for (TherapySessionDTO s : all) list.add(new TherapySessionTM(s.getSessionId(), s.getDate(), s.getTime(), s.getStatus(), s.getPatientName(), s.getTherapistName(), s.getProgramName()));
            tblSessions.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
