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

    public static void generateReport(String jrxmlPath, Map<String, Object> params) {
        try {
            var inputStream = JasperReportUtil.class.getResourceAsStream(jrxmlPath);
            if (inputStream == null) {
                new Alert(Alert.AlertType.ERROR, "Report template not found: " + jrxmlPath).showAndWait();
                return;
            }

            JasperReport report = JasperCompileManager.compileReport(inputStream);

            Session session = FactoryConfiguration.getInstance().getSession();
            Connection connection = session.doReturningWork(conn -> conn);

            if (params == null) params = new HashMap<>();
            JasperPrint print = JasperFillManager.fillReport(report, params, connection);

            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Serenity - Report");
            viewer.setVisible(true);

            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to generate report: " + e.getMessage()).showAndWait();
        }
    }


    public static void generateReport(String jrxmlPath) {
        generateReport(jrxmlPath, null);
    }
}
