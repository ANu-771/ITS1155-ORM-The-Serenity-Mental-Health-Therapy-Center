package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.App;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private ComboBox<String> cmbRole;
    @FXML
    private Label lblError;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbRole.setItems(FXCollections.observableArrayList("Admin", "Receptionist"));
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        lblError.setVisible(false);
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = cmbRole.getValue();

        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                role == null || role.trim().isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {

            String newUserId = userBO.getNextId();
            UserDTO newUser = new UserDTO(newUserId, username.trim(), password, role);
            userBO.saveUser(newUser);
            new Alert(Alert.AlertType.INFORMATION, "Registration successful! You can now log in.").showAndWait();
            handleBackToLogin(event);

        } catch (Exception e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("duplicate")) {
                showError("Username already exists. Please choose another.");
            } else {
                showError("Registration failed. Please try again.");
            }
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Login.fxml"));
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.setTitle("Serenity Mental Health Therapy Center");
        } catch (Exception e) {
            showError("Could not load Login page.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}

