package lk.ijse.theserenitymentalhealththerapycenter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.config.FactoryConfiguration;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize Hibernate SessionFactory
        FactoryConfiguration.getInstance();

        // Create default admin account if no users exist
        try {
            UserBO userBO = BOFactory.getInstance().getBO(BOFactory.BOType.USER);
            userBO.createDefaultAdmin();
        } catch (Exception e) {
            System.err.println("Warning: Could not create default admin - " + e.getMessage());
        }

        // Load Login screen
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Serenity Mental Health Therapy Center");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }
}
