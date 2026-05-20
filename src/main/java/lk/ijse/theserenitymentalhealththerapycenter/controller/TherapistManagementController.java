package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapistBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapistDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.TherapistTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapistManagementController implements Initializable {

    @FXML private TextField txtId, txtName, txtSpecialization, txtContact, txtEmail, txtSearch;
    @FXML private TableView<TherapistTM> tblTherapists;

    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTable();
        tblTherapists.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) { txtId.setText(nw.getId()); txtName.setText(nw.getName()); txtSpecialization.setText(nw.getSpecialization()); txtContact.setText(nw.getContactNumber()); txtEmail.setText(nw.getEmail()); }
        });
    }

    @FXML void handleSave(ActionEvent e) {
        try {
            therapistBO.saveTherapist(new TherapistDTO(txtId.getText(), txtName.getText(), txtSpecialization.getText(), txtContact.getText(), txtEmail.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Therapist saved!").showAndWait(); loadTable(); handleClear(null);
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleUpdate(ActionEvent e) {
        try {
            therapistBO.updateTherapist(new TherapistDTO(txtId.getText(), txtName.getText(), txtSpecialization.getText(), txtContact.getText(), txtEmail.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Therapist updated!").showAndWait(); loadTable(); handleClear(null);
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleDelete(ActionEvent e) {
        try { therapistBO.deleteTherapist(txtId.getText()); new Alert(Alert.AlertType.INFORMATION, "Deleted!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
    }

    @FXML void handleClear(ActionEvent e) { txtId.clear(); txtName.clear(); txtSpecialization.clear(); txtContact.clear(); txtEmail.clear(); if (txtSearch != null) txtSearch.clear(); }

    @FXML void handleSearch(javafx.scene.input.KeyEvent e) {
        String id = txtSearch.getText().trim();
        if (id.isEmpty()) { loadTable(); return; }
        try {
            TherapistDTO t = therapistBO.searchTherapist(id);
            ObservableList<TherapistTM> list = FXCollections.observableArrayList();
            if (t != null) list.add(new TherapistTM(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail()));
            tblTherapists.setItems(list);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTable() {
        try {
            List<TherapistDTO> all = therapistBO.getAllTherapists();
            ObservableList<TherapistTM> list = FXCollections.observableArrayList();
            for (TherapistDTO t : all) list.add(new TherapistTM(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail()));
            tblTherapists.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
