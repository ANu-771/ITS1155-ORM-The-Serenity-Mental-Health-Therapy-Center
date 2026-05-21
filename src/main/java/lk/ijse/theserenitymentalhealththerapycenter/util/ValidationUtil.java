package lk.ijse.theserenitymentalhealththerapycenter.util;

import javafx.scene.control.*;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Centralized validation utility for all management controllers.
 * Provides regex validation, required-field checks, and confirmation dialogs.
 */
public class ValidationUtil {

    // ===== Regex Patterns =====
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]{2,100}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+94|0)\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|;:'\",.<>?/]).{8,}$");

    // ===== Field Validation Methods =====

    /**
     * Validate a name field — only letters and spaces.
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validate a phone number — Sri Lanka format: +94XXXXXXXXX or 0XXXXXXXXX.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validate an email address.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate a password — min 8 chars, at least 1 number, at least 1 special character.
     */
    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    // ===== UI Feedback Helpers =====

    /**
     * Mark a TextField as valid (reset border).
     */
    public static void setValid(TextField field) {
        field.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
    }

    /**
     * Mark a TextField as invalid (red border).
     */
    public static void setInvalid(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
    }

    /**
     * Validate a TextField with a custom regex pattern.
     * Returns true if valid, false if invalid (and sets red border).
     */
    public static boolean validateField(TextField field, Pattern pattern) {
        if (field.getText() == null || field.getText().trim().isEmpty() || !pattern.matcher(field.getText().trim()).matches()) {
            setInvalid(field);
            return false;
        }
        setValid(field);
        return true;
    }

    /**
     * Validate a TextField is not empty (required field check).
     */
    public static boolean validateRequired(TextField field) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            setInvalid(field);
            return false;
        }
        setValid(field);
        return true;
    }

    /**
     * Validate a ComboBox is not empty (required field check).
     */
    public static boolean validateRequired(ComboBox<?> combo) {
        if (combo.getValue() == null) {
            combo.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 8;");
            return false;
        }
        combo.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        return true;
    }

    /**
     * Validate a TextArea is not empty (required field check).
     */
    public static boolean validateRequired(TextArea area) {
        if (area.getText() == null || area.getText().trim().isEmpty()) {
            area.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 8;");
            return false;
        }
        area.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        return true;
    }

    // ===== Convenience Validators =====

    public static boolean validateName(TextField field) {
        return validateField(field, NAME_PATTERN);
    }

    public static boolean validatePhone(TextField field) {
        return validateField(field, PHONE_PATTERN);
    }

    public static boolean validateEmail(TextField field) {
        return validateField(field, EMAIL_PATTERN);
    }

    public static boolean validatePassword(TextField field) {
        return validateField(field, PASSWORD_PATTERN);
    }

    // ===== Reset All Fields =====

    /**
     * Reset border styles for multiple TextFields.
     */
    public static void resetStyles(TextField... fields) {
        for (TextField f : fields) setValid(f);
    }

    // ===== Confirmation Dialog =====

    /**
     * Show a delete confirmation dialog.
     * Returns true if user clicks OK/Yes.
     */
    public static boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Record");
        alert.setContentText("Are you sure you want to delete this record? This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show a custom confirmation dialog.
     */
    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Show a required-fields-missing error alert.
     */
    public static void showRequiredFieldsError() {
        new Alert(Alert.AlertType.ERROR, "Please fill in all required details before saving.").showAndWait();
    }
}
