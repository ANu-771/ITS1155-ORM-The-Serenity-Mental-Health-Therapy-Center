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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private TextField txtId, txtUsername, txtSearch;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private TableView<UserTM> tblUsers;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cmbRole.setItems(FXCollections.observableArrayList("Admin", "Receptionist"));
        loadTable();
        tblUsers.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                txtId.setText(newVal.getId());
                txtUsername.setText(newVal.getUsername());
                cmbRole.setValue(newVal.getRole());
                txtPassword.clear();
            }
        });
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            UserDTO dto = new UserDTO(txtId.getText(), txtUsername.getText(), txtPassword.getText(), cmbRole.getValue());
            userBO.saveUser(dto);
            showAlert(Alert.AlertType.INFORMATION, "User saved successfully!");
            loadTable();
            handleClear(null);
        } catch (DuplicateEntryException | InvalidInputException e) {
            showAlert(Alert.AlertType.WARNING, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to save user: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdate(ActionEvent event) {
        try {
            UserDTO dto = new UserDTO(txtId.getText(), txtUsername.getText(), txtPassword.getText(), cmbRole.getValue());
            userBO.updateUser(dto);
            showAlert(Alert.AlertType.INFORMATION, "User updated successfully!");
            loadTable();
            handleClear(null);
        } catch (DuplicateEntryException | InvalidInputException e) {
            showAlert(Alert.AlertType.WARNING, e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to update user: " + e.getMessage());
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        String id = txtId.getText();
        if (id.isEmpty()) { showAlert(Alert.AlertType.WARNING, "Please select a user to delete"); return; }
        try {
            userBO.deleteUser(id);
            showAlert(Alert.AlertType.INFORMATION, "User deleted successfully!");
            loadTable();
            handleClear(null);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Failed to delete user: " + e.getMessage());
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        txtId.clear(); txtUsername.clear(); txtPassword.clear(); cmbRole.setValue(null); txtSearch.clear();
    }

    @FXML
    void handleSearch(javafx.scene.input.KeyEvent event) {
        String id = txtSearch.getText().trim();
        if (id.isEmpty()) { loadTable(); return; }
        try {
            UserDTO user = userBO.searchUser(id);
            ObservableList<UserTM> list = FXCollections.observableArrayList();
            if (user != null) list.add(new UserTM(user.getId(), user.getUsername(), user.getRole()));
            tblUsers.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadTable() {
        try {
            List<UserDTO> users = userBO.getAllUsers();
            ObservableList<UserTM> list = FXCollections.observableArrayList();
            for (UserDTO u : users) list.add(new UserTM(u.getId(), u.getUsername(), u.getRole()));
            tblUsers.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }
}
