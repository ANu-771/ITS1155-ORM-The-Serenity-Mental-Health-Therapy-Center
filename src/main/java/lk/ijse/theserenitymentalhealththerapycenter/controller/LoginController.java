package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.HelloApplication;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.exception.AuthenticationException;
import lk.ijse.theserenitymentalhealththerapycenter.util.SessionContext;

public class LoginController {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private TextField txtPasswordVisible;
    @FXML
    private CheckBox chkShowPassword;
    @FXML
    private Label lblError;
    @FXML
    private ImageView imgLogo;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = chkShowPassword.isSelected()
                ? txtPasswordVisible.getText().trim()
                : txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            UserDTO user = userBO.authenticateUser(username, password);

            SessionContext.setCurrentUser(user);

            Stage stage = (Stage) txtUsername.getScene().getWindow();

            String fxmlFile;
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                fxmlFile = "view/AdminDashboard.fxml";
            } else {
                fxmlFile = "view/ReceptionistDashboard.fxml";
            }

            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.setTitle("Serenity - " + user.getRole() + " Dashboard");

        } catch (AuthenticationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Login failed. Please try again.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleTogglePassword(ActionEvent event) {
        if (chkShowPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPassword.setVisible(false);
            txtPassword.setManaged(false);
            txtPasswordVisible.setVisible(true);
            txtPasswordVisible.setManaged(true);
            txtPasswordVisible.requestFocus();
        } else {
            // Hide password
            txtPassword.setText(txtPasswordVisible.getText());
            txtPasswordVisible.setVisible(false);
            txtPasswordVisible.setManaged(false);
            txtPassword.setVisible(true);
            txtPassword.setManaged(true);
            txtPassword.requestFocus();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }
}
