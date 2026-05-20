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
        loadTable();
        tblPatients.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtId.setText(nw.getId()); txtName.setText(nw.getName()); txtDob.setText(nw.getDob());
                txtContact.setText(nw.getContactNumber()); txtEmail.setText(nw.getEmail()); txtRegDate.setText(nw.getRegistrationDate());
            }
        });
    }

    @FXML void handleSave(ActionEvent e) {
        try {
            patientBO.savePatient(new PatientDTO(txtId.getText(), txtName.getText(), txtDob.getText(), txtContact.getText(), txtEmail.getText(), txtMedicalHistory.getText(), txtRegDate.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Patient saved!").showAndWait(); loadTable(); handleClear(null);
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleUpdate(ActionEvent e) {
        try {
            patientBO.updatePatient(new PatientDTO(txtId.getText(), txtName.getText(), txtDob.getText(), txtContact.getText(), txtEmail.getText(), txtMedicalHistory.getText(), txtRegDate.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Patient updated!").showAndWait(); loadTable(); handleClear(null);
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleDelete(ActionEvent e) {
        try { patientBO.deletePatient(txtId.getText()); new Alert(Alert.AlertType.INFORMATION, "Deleted!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
    }

    @FXML void handleClear(ActionEvent e) { txtId.clear(); txtName.clear(); txtDob.clear(); txtContact.clear(); txtEmail.clear(); txtMedicalHistory.clear(); txtRegDate.clear(); if (txtSearch != null) txtSearch.clear(); }

    @FXML void handleSearch(javafx.scene.input.KeyEvent e) {
        String q = txtSearch.getText().trim();
        if (q.isEmpty()) { loadTable(); return; }
        try {
            List<PatientDTO> results = patientBO.searchPatientsByName(q);
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : results) list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getRegistrationDate()));
            tblPatients.setItems(list);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTable() {
        try {
            List<PatientDTO> all = patientBO.getAllPatients();
            ObservableList<PatientTM> list = FXCollections.observableArrayList();
            for (PatientDTO p : all) list.add(new PatientTM(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getRegistrationDate()));
            tblPatients.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
