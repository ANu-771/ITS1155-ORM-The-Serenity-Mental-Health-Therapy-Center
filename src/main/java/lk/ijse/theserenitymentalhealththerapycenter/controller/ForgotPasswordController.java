package lk.ijse.theserenitymentalhealththerapycenter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.App;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.util.MailUtil;

public class ForgotPasswordController {

    @FXML
    private VBox step1Container;
    @FXML
    private VBox step2Container;
    @FXML
    private VBox step3Container;

    @FXML
    private TextField txtUsernameOrEmail;
    @FXML
    private TextField txtOTP;
    @FXML
    private PasswordField txtNewPassword;
    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private Label lblError;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblOtpDestination;
    @FXML
    private Button btnSendOTP;

    private final UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);
    
    private String generatedOTP;
    private String targetUsername;

    @FXML
    public void handleSendOTP(ActionEvent event) {
        lblError.setVisible(false);
        lblStatus.setVisible(false);

        String input = txtUsernameOrEmail.getText();
        if (input == null || input.trim().isEmpty()) {
            showError("Please enter your username or email address.");
            return;
        }

        try {
            UserDTO user = userBO.getUserByUsernameOrEmail(input.trim());
            if (user == null) {
                showError("No account found matching this username or email.");
                return;
            }

            String email = user.getEmail();
            if (email == null || email.trim().isEmpty()) {
                showError("No email address configured for this account. Please contact system administrator.");
                return;
            }

            // Generate 6-digit random code
            int code = 100000 + new java.util.Random().nextInt(900000);
            generatedOTP = String.valueOf(code);
            targetUsername = user.getUsername();

            lblStatus.setText("Sending verification code to your email...");
            lblStatus.setVisible(true);
            btnSendOTP.setDisable(true);

            new Thread(() -> {
                try {
                    MailUtil.sendOTP(email, generatedOTP);
                    javafx.application.Platform.runLater(() -> {
                        lblStatus.setVisible(false);
                        btnSendOTP.setDisable(false);
                        new Alert(Alert.AlertType.INFORMATION, "Verification OTP code sent successfully!").showAndWait();
                        
                        // Transition Step 1 -> Step 2
                        step1Container.setVisible(false);
                        step1Container.setManaged(false);
                        step2Container.setVisible(true);
                        step2Container.setManaged(true);
                        lblOtpDestination.setText("We have sent a 6-digit OTP code to: " + maskEmail(email));
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        lblStatus.setVisible(false);
                        btnSendOTP.setDisable(false);
                        showError("Failed to send email. Please check your connection or email setup.");
                    });
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred. Please try again.");
        }
    }

    @FXML
    public void handleVerifyOTP(ActionEvent event) {
        lblError.setVisible(false);
        String enteredOtp = txtOTP.getText();
        if (enteredOtp == null || enteredOtp.trim().isEmpty()) {
            showError("Please enter the verification code.");
            return;
        }

        if (enteredOtp.trim().equals(generatedOTP)) {
            new Alert(Alert.AlertType.INFORMATION, "Code verified successfully!").showAndWait();
            
            // Transition Step 2 -> Step 3
            step2Container.setVisible(false);
            step2Container.setManaged(false);
            step3Container.setVisible(true);
            step3Container.setManaged(true);
        } else {
            showError("Invalid verification code. Please check your email and try again.");
        }
    }

    @FXML
    public void handleResetPassword(ActionEvent event) {
        lblError.setVisible(false);
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (newPassword == null || newPassword.trim().isEmpty()) {
            showError("Please enter your new password.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }

        try {
            boolean success = userBO.resetPassword(targetUsername, newPassword);
            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Password reset successfully!").showAndWait();
                handleBackToLogin(event);
            } else {
                showError("Unable to update password. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while resetting the password.");
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) txtUsernameOrEmail.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Login.fxml"));
            Parent root = loader.load();
            stage.getScene().setRoot(root);
            stage.setTitle("Serenity Mental Health Therapy Center");
        } catch (Exception e) {
            showError("Could not load Login view.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "your email";
        int atIdx = email.indexOf("@");
        String prefix = email.substring(0, atIdx);
        String suffix = email.substring(atIdx);
        if (prefix.length() <= 2) {
            return prefix + "***" + suffix;
        }
        return prefix.substring(0, 2) + "***" + prefix.substring(prefix.length() - 1) + suffix;
    }
}
