package lk.ijse.theserenitymentalhealththerapycenter.util;

import javafx.scene.control.*;

import java.util.Optional;
import java.util.regex.Pattern;


public class ValidationUtil {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z .'-]{2,100}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+94|0)\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}|;:'\",.<>?/]).{8,}$");

    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }


    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }


    public static boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }


    public static void setValid(TextField field) {
        field.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8; -fx-background-radius: 8;");
    }


    public static void setInvalid(TextField field) {
        field.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1.5; -fx-border-radius: 8; -fx-background-radius: 8;");
    }


    public static boolean validateField(TextField field, Pattern pattern) {
        if (field.getText() == null || field.getText().trim().isEmpty() || !pattern.matcher(field.getText().trim()).matches()) {
            setInvalid(field);
            return false;
        }
        setValid(field);
        return true;
    }


    public static boolean validateRequired(TextField field) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            setInvalid(field);
            return false;
        }
        setValid(field);
        return true;
    }


    public static boolean validateRequired(ComboBox<?> combo) {
        if (combo.getValue() == null) {
            combo.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 8;");
            return false;
        }
        combo.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        return true;
    }


    public static boolean validateRequired(TextArea area) {
        if (area.getText() == null || area.getText().trim().isEmpty()) {
            area.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 8;");
            return false;
        }
        area.setStyle("-fx-border-color: #dee2e6; -fx-border-radius: 8;");
        return true;
    }


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


    public static void resetStyles(TextField... fields) {
        for (TextField f : fields) setValid(f);
    }


    public static boolean confirmDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Record");
        alert.setContentText("Are you sure you want to delete this record? This action cannot be undone.");
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }


    public static void showRequiredFieldsError() {
        new Alert(Alert.AlertType.ERROR, "Please fill in all required details before saving.").showAndWait();
    }
}
