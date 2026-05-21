package lk.ijse.theserenitymentalhealththerapycenter.util;

import javafx.scene.control.Alert;
import lk.ijse.theserenitymentalhealththerapycenter.config.FactoryConfiguration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import org.hibernate.Session;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class JasperReportUtil {

    /**
     * Compile, fill, and display a JasperReport in a viewer window.
     *
     * @param jrxmlPath classpath path to the .jrxml file (e.g. "/reports/PaymentReport.jrxml")
     * @param params    optional report parameters (can be null)
     */
    public static void generateReport(String jrxmlPath, Map<String, Object> params) {
        try {
            // Load the JRXML from classpath
            var inputStream = JasperReportUtil.class.getResourceAsStream(jrxmlPath);
            if (inputStream == null) {
                new Alert(Alert.AlertType.ERROR, "Report template not found: " + jrxmlPath).showAndWait();
                return;
            }

            // Compile
            JasperReport report = JasperCompileManager.compileReport(inputStream);

            // Get DB connection from Hibernate
            Session session = FactoryConfiguration.getInstance().getSession();
            Connection connection = session.doReturningWork(conn -> conn);

            // Fill
            if (params == null) params = new HashMap<>();
            JasperPrint print = JasperFillManager.fillReport(report, params, connection);

            // Display in viewer
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Serenity - Report");
            viewer.setVisible(true);

            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to generate report: " + e.getMessage()).showAndWait();
        }
    }

    /**
     * Convenience method — no params
     */
    public static void generateReport(String jrxmlPath) {
        generateReport(jrxmlPath, null);
    }
}
