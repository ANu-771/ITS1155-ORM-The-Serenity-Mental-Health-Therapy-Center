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
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

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
        txtProgramId.setEditable(false);
        generateNextId();
        loadTable();
        tblPrograms.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtProgramId.setText(nw.getProgramId());
                txtName.setText(nw.getName());
                txtDuration.setText(nw.getDuration());
                txtFee.setText(String.valueOf(nw.getFee()));
                txtDescription.setText(nw.getDescription());
                ValidationUtil.resetStyles(txtProgramId, txtName, txtDuration, txtFee);
            }
        });
    }

    private void generateNextId() {
        try { txtProgramId.setText(programBO.getNextId()); } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    void handleSave(ActionEvent e) {
        ValidationUtil.resetStyles(txtProgramId, txtName, txtDuration, txtFee);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtDuration)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtFee)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            double fee = Double.parseDouble(txtFee.getText().trim());
            programBO.saveProgram(new TherapyProgramDTO(
                    txtProgramId.getText().trim(), txtName.getText().trim(),
                    txtDuration.getText().trim(), fee, txtDescription.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Program saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (NumberFormatException ex) {
            ValidationUtil.setInvalid(txtFee);
            new Alert(Alert.AlertType.WARNING, "Invalid fee amount. Please enter a valid number.").showAndWait();
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        ValidationUtil.resetStyles(txtProgramId, txtName, txtDuration, txtFee);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtProgramId)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtDuration)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtFee)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            double fee = Double.parseDouble(txtFee.getText().trim());
            programBO.updateProgram(new TherapyProgramDTO(
                    txtProgramId.getText().trim(), txtName.getText().trim(),
                    txtDuration.getText().trim(), fee, txtDescription.getText()));
            new Alert(Alert.AlertType.INFORMATION, "Program updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (NumberFormatException ex) {
            ValidationUtil.setInvalid(txtFee);
            new Alert(Alert.AlertType.WARNING, "Invalid fee amount. Please enter a valid number.").showAndWait();
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent e) {
        String id = txtProgramId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a program to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) return;

        try {
            programBO.deleteProgram(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Program deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtName.clear(); txtDuration.clear();
        txtFee.clear(); txtDescription.clear();
        if (txtSearch != null) txtSearch.clear();
        ValidationUtil.resetStyles(txtProgramId, txtName, txtDuration, txtFee);
        generateNextId();
    }

    @FXML
    void handleSearch(javafx.scene.input.KeyEvent e) {
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
