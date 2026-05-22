package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.exception.AuthenticationException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.DuplicateEntryException;
import lk.ijse.theserenitymentalhealththerapycenter.util.SessionContext;

import java.net.URL;
import java.util.ResourceBundle;

public class ChangeCredentialsController implements Initializable {

    @FXML
    private PasswordField txtCurrentPassword;
    @FXML
    private TextField txtNewUsername;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblInfo;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserDTO currentUser = SessionContext.getCurrentUser();
        if (currentUser != null) {
            txtNewUsername.setPromptText("Current: " + currentUser.getUsername());
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        lblMessage.setVisible(false);

        String currentPassword = txtCurrentPassword.getText();
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            showError("Current password is required to make changes.");
            return;
        }

        String newUsername = txtNewUsername.getText().trim();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        boolean usernameProvided = !newUsername.isEmpty();
        boolean passwordProvided = newPassword != null && !newPassword.trim().isEmpty();

        if (!usernameProvided && !passwordProvided) {
            showError("Please enter a new username or new password (or both).");
            return;
        }

        if (passwordProvided) {
            if (!newPassword.equals(confirmPassword)) {
                showError("New password and confirmation do not match.");
                return;
            }
        }

        try {
            UserDTO currentUser = SessionContext.getCurrentUser();
            if (currentUser == null) {
                showError("Session expired. Please log in again.");
                return;
            }

            userBO.changeCredentials(
                    currentUser.getId(),
                    currentPassword.trim(),
                    usernameProvided ? newUsername : null,
                    passwordProvided ? newPassword : null
            );

            if (usernameProvided) {
                currentUser = new UserDTO(currentUser.getId(), newUsername, "", currentUser.getRole());
                SessionContext.setCurrentUser(currentUser);
                txtNewUsername.setPromptText("Current: " + newUsername);
            }

            showSuccess("Credentials updated successfully!");
            handleClear(null);

        } catch (AuthenticationException e) {
            showError(e.getMessage());
        } catch (DuplicateEntryException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Failed to update credentials: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleClear(ActionEvent event) {
        txtCurrentPassword.clear();
        txtNewUsername.clear();
        txtNewPassword.clear();
        txtConfirmPassword.clear();
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #e53935; -fx-font-size: 12px;");
        lblMessage.setVisible(true);
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: #2e7d32; -fx-font-size: 12px; -fx-font-weight: bold;");
        lblMessage.setVisible(true);
    }
}
