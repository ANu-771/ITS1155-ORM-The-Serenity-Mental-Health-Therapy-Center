package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.TherapyProgramTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapyProgramManagementController implements Initializable {

    @FXML private TextField txtProgramId, txtName, txtDuration, txtFee, txtSearch;
    @FXML private TextArea txtDescription;
    @FXML private TableView<TherapyProgramTM> tblPrograms;

    private final TherapyProgramBO programBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_PROGRAM);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadTable();
        tblPrograms.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) { txtProgramId.setText(nw.getProgramId()); txtName.setText(nw.getName()); txtDuration.setText(nw.getDuration()); txtFee.setText(String.valueOf(nw.getFee())); txtDescription.setText(nw.getDescription()); }
        });
    }

    @FXML void handleSave(ActionEvent e) {
        try {
            double fee = Double.parseDouble(txtFee.getText());
            programBO.saveProgram(new TherapyProgramDTO(txtProgramId.getText(), txtName.getText(), txtDuration.getText(), fee, txtDescription.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Program saved!").showAndWait(); loadTable(); handleClear(null);
        } catch (NumberFormatException ex) { new Alert(Alert.AlertType.WARNING, "Invalid fee amount").showAndWait();
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleUpdate(ActionEvent e) {
        try {
            double fee = Double.parseDouble(txtFee.getText());
            programBO.updateProgram(new TherapyProgramDTO(txtProgramId.getText(), txtName.getText(), txtDuration.getText(), fee, txtDescription.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Program updated!").showAndWait(); loadTable(); handleClear(null);
        } catch (NumberFormatException ex) { new Alert(Alert.AlertType.WARNING, "Invalid fee amount").showAndWait();
        } catch (InvalidInputException ex) { new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait(); }
    }

    @FXML void handleDelete(ActionEvent e) {
        try { programBO.deleteProgram(txtProgramId.getText()); new Alert(Alert.AlertType.INFORMATION, "Deleted!").showAndWait(); loadTable(); handleClear(null);
        } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait(); }
    }

    @FXML void handleClear(ActionEvent e) { txtProgramId.clear(); txtName.clear(); txtDuration.clear(); txtFee.clear(); txtDescription.clear(); if (txtSearch != null) txtSearch.clear(); }

    @FXML void handleSearch(javafx.scene.input.KeyEvent e) {
        String id = txtSearch.getText().trim();
        if (id.isEmpty()) { loadTable(); return; }
        try {
            TherapyProgramDTO p = programBO.searchProgram(id);
            ObservableList<TherapyProgramTM> list = FXCollections.observableArrayList();
            if (p != null) list.add(new TherapyProgramTM(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getDescription()));
            tblPrograms.setItems(list);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void loadTable() {
        try {
            List<TherapyProgramDTO> all = programBO.getAllPrograms();
            ObservableList<TherapyProgramTM> list = FXCollections.observableArrayList();
            for (TherapyProgramDTO p : all) list.add(new TherapyProgramTM(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getDescription()));
            tblPrograms.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
