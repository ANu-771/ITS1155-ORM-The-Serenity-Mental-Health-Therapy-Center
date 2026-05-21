module lk.ijse.theserenitymentalhealththerapycenter {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires jbcrypt;
    requires java.sql;
    requires jasperreports;
    requires java.desktop;

    opens lk.ijse.theserenitymentalhealththerapycenter to javafx.fxml;
    opens lk.ijse.theserenitymentalhealththerapycenter.entity to org.hibernate.orm.core;
    opens lk.ijse.theserenitymentalhealththerapycenter.config to org.hibernate.orm.core;
    opens lk.ijse.theserenitymentalhealththerapycenter.controller to javafx.fxml;

    exports lk.ijse.theserenitymentalhealththerapycenter;
    exports lk.ijse.theserenitymentalhealththerapycenter.dto;
    exports lk.ijse.theserenitymentalhealththerapycenter.dto.tm;
    exports lk.ijse.theserenitymentalhealththerapycenter.entity;
}