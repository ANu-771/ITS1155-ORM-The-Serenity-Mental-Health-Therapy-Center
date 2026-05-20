package lk.ijse.theserenitymentalhealththerapycenter.dao;

import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;

    private DAOFactory() {}

    public static DAOFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DAOFactory();
        }
        return daoFactory;
    }

    public enum DAOType {
        USER, THERAPIST, THERAPY_PROGRAM, PATIENT, THERAPY_SESSION, PAYMENT
    }

    public <T> T getDAO(DAOType type) {
        switch (type) {
            case USER:
                return (T) new UserDAOImpl();
            case THERAPIST:
                return (T) new TherapistDAOImpl();
            case THERAPY_PROGRAM:
                return (T) new TherapyProgramDAOImpl();
            case PATIENT:
                return (T) new PatientDAOImpl();
            case THERAPY_SESSION:
                return (T) new TherapySessionDAOImpl();
            case PAYMENT:
                return (T) new PaymentDAOImpl();
            default:
                return null;
        }
    }
}
