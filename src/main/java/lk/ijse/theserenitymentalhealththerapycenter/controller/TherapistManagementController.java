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
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TherapistManagementController implements Initializable {

    @FXML
    private TextField txtId, txtName, txtSpecialization, txtContact, txtEmail, txtSearch;
    @FXML
    private TableView<TherapistTM> tblTherapists;

    private final TherapistBO therapistBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPIST);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtId.setEditable(false);
        generateNextId();
        loadTable();
        tblTherapists.getSelectionModel().selectedItemProperty().addListener((obs, old, nw) -> {
            if (nw != null) {
                txtId.setText(nw.getId());
                txtName.setText(nw.getName());
                txtSpecialization.setText(nw.getSpecialization());
                txtContact.setText(nw.getContactNumber());
                txtEmail.setText(nw.getEmail());
                ValidationUtil.resetStyles(txtId, txtName, txtSpecialization, txtContact, txtEmail);
            }
        });
    }

    private void generateNextId() {
        try {
            txtId.setText(therapistBO.getNextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent e) {
        if (!validateInput()) {
            return;
        }

        try {
            therapistBO.saveTherapist(new TherapistDTO(
                    txtId.getText().trim(), txtName.getText().trim(),
                    txtSpecialization.getText().trim(), txtContact.getText().trim(),
                    txtEmail.getText().trim()));
            new Alert(Alert.AlertType.INFORMATION, "Therapist saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (InvalidInputException ex) {
            new Alert(Alert.AlertType.WARNING, ex.getMessage()).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent e) {
        if (!ValidationUtil.validateRequired(txtId)) {
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to update.").showAndWait();
            return;
        }

        if (!validateInput()) {
            return;
        }

        try {
            therapistBO.updateTherapist(new TherapistDTO(
                    txtId.getText().trim(), txtName.getText().trim(),
                    txtSpecialization.getText().trim(), txtContact.getText().trim(),
                    txtEmail.getText().trim()));
            new Alert(Alert.AlertType.INFORMATION, "Therapist updated successfully!").showAndWait();
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
            new Alert(Alert.AlertType.WARNING, "Please select a therapist to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) return;

        try {
            therapistBO.deleteTherapist(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "Therapist deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent e) {
        txtName.clear();
        txtSpecialization.clear();
        txtContact.clear();
        txtEmail.clear();
        if (txtSearch != null) txtSearch.clear();
        ValidationUtil.resetStyles(txtId, txtName, txtSpecialization, txtContact, txtEmail);
        generateNextId();
    }

    @FXML
    void handleSearch(javafx.scene.input.KeyEvent e) {
        String id = txtSearch.getText().trim();
        if (id.isEmpty()) {
            loadTable();
            return;
        }
        try {
            TherapistDTO t = therapistBO.searchTherapist(id);
            ObservableList<TherapistTM> list = FXCollections.observableArrayList();
            if (t != null)
                list.add(new TherapistTM(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail()));
            tblTherapists.setItems(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateInput() {
        ValidationUtil.resetStyles(txtId, txtName, txtSpecialization, txtContact, txtEmail);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtName)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtSpecialization)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtContact)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return false;
        }

        if (!ValidationUtil.isValidName(txtName.getText())) {
            ValidationUtil.setInvalid(txtName);
            new Alert(Alert.AlertType.ERROR, "Invalid Name. The name can only contain letters, spaces, hyphens, and titles with periods (like Dr.).").showAndWait();
            txtName.requestFocus();
            return false;
        }

        if (!ValidationUtil.isValidPhone(txtContact.getText())) {
            ValidationUtil.setInvalid(txtContact);
            new Alert(Alert.AlertType.ERROR, "Invalid Contact Number. The contact number must be exactly 10 digits long.").showAndWait();
            txtContact.requestFocus();
            return false;
        }

        if (txtEmail.getText() != null && !txtEmail.getText().trim().isEmpty()) {
            if (!ValidationUtil.isValidEmail(txtEmail.getText())) {
                ValidationUtil.setInvalid(txtEmail);
                new Alert(Alert.AlertType.ERROR, "Invalid Email. Please enter a valid email address format.").showAndWait();
                txtEmail.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void loadTable() {
        try {
            List<TherapistDTO> all = therapistBO.getAllTherapists();
            ObservableList<TherapistTM> list = FXCollections.observableArrayList();
            for (TherapistDTO t : all)
                list.add(new TherapistTM(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail()));
            tblTherapists.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
