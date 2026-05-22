package lk.ijse.theserenitymentalhealththerapycenter.config;

import lk.ijse.theserenitymentalhealththerapycenter.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Properties;

public class FactoryConfiguration {
    private static FactoryConfiguration factoryConfiguration;
    private final SessionFactory sessionFactory;

    private FactoryConfiguration() {
        Configuration configuration = new Configuration();

        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("hibernate.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load hibernate.properties", e);
        }

        configuration.setProperties(properties);

        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Therapist.class);
        configuration.addAnnotatedClass(TherapyProgram.class);
        configuration.addAnnotatedClass(Patient.class);
        configuration.addAnnotatedClass(TherapySession.class);
        configuration.addAnnotatedClass(Payment.class);

        sessionFactory = configuration.buildSessionFactory();
    }

    public static FactoryConfiguration getInstance() {
        if (factoryConfiguration == null) {
            synchronized (FactoryConfiguration.class) {
                if (factoryConfiguration == null) {
                    factoryConfiguration = new FactoryConfiguration();
                }
            }
        }
        return factoryConfiguration;
    }

    public Session getSession() {
        return sessionFactory.openSession();
    }
}


//Anu