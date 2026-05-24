package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.tm.UserTM;
import lk.ijse.theserenitymentalhealththerapycenter.exception.DuplicateEntryException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.util.ValidationUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML
    private TextField txtId, txtUsername, txtSearch, txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private TableView<UserTM> tblUsers;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbRole.setItems(FXCollections.observableArrayList("Admin", "Receptionist"));
        txtId.setEditable(false);
        generateNextId();
        loadTable();
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                txtId.setText(newVal.getId());
                txtUsername.setText(newVal.getUsername());
                cmbRole.setValue(newVal.getRole());
                txtEmail.setText(newVal.getEmail() != null ? newVal.getEmail() : "");
                txtPassword.clear();
                ValidationUtil.resetStyles(txtId, txtUsername, txtEmail);
            }
        });
    }

    private void generateNextId() {
        try {
            txtId.setText(userBO.getNextId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        ValidationUtil.resetStyles(txtId, txtUsername, txtEmail);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtUsername)) allFilled = false;
        if (!ValidationUtil.validateEmail(txtEmail)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtPassword)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbRole)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }

        try {
            UserDTO dto = new UserDTO(txtId.getText().trim(), txtUsername.getText().trim(), txtPassword.getText(), cmbRole.getValue(), txtEmail.getText().trim());
            userBO.saveUser(dto);
            new Alert(Alert.AlertType.INFORMATION, "User saved successfully!").showAndWait();
            loadTable();
            handleClear(null);
            generateNextId();
        } catch (DuplicateEntryException | InvalidInputException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to save user: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        ValidationUtil.resetStyles(txtId, txtUsername, txtEmail);

        boolean allFilled = true;
        if (!ValidationUtil.validateRequired(txtId)) allFilled = false;
        if (!ValidationUtil.validateRequired(txtUsername)) allFilled = false;
        if (!ValidationUtil.validateEmail(txtEmail)) allFilled = false;
        if (!ValidationUtil.validateRequired(cmbRole)) allFilled = false;

        if (!allFilled) {
            ValidationUtil.showRequiredFieldsError();
            return;
        }


        try {
            UserDTO dto = new UserDTO(txtId.getText().trim(), txtUsername.getText().trim(), txtPassword.getText(), cmbRole.getValue(), txtEmail.getText().trim());
            userBO.updateUser(dto);
            new Alert(Alert.AlertType.INFORMATION, "User updated successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (DuplicateEntryException | InvalidInputException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to update user: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        String id = txtId.getText();
        if (id == null || id.trim().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please select a user to delete.").showAndWait();
            return;
        }

        if (!ValidationUtil.confirmDelete()) return;

        try {
            userBO.deleteUser(id.trim());
            new Alert(Alert.AlertType.INFORMATION, "User deleted successfully!").showAndWait();
            loadTable();
            handleClear(null);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to delete user: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        txtUsername.clear();
        txtEmail.clear();
        txtPassword.clear();
        cmbRole.setValue(null);
        if (txtSearch != null) txtSearch.clear();
        ValidationUtil.resetStyles(txtId, txtUsername, txtEmail);
        generateNextId();
    }

    @FXML
    void handleSearch(javafx.scene.input.KeyEvent event) {
        String id = txtSearch.getText().trim();
        if (id.isEmpty()) {
            loadTable();
            return;
        }
        try {
            UserDTO user = userBO.searchUser(id);
            ObservableList<UserTM> list = FXCollections.observableArrayList();
            if (user != null) list.add(new UserTM(user.getId(), user.getUsername(), user.getRole(), user.getEmail()));
            tblUsers.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTable() {
        try {
            List<UserDTO> users = userBO.getAllUsers();
            ObservableList<UserTM> list = FXCollections.observableArrayList();
            for (UserDTO u : users) list.add(new UserTM(u.getId(), u.getUsername(), u.getRole(), u.getEmail()));
            tblUsers.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
