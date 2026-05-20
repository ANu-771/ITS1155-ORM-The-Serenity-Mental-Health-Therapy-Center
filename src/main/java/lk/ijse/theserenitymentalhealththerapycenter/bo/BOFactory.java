package lk.ijse.theserenitymentalhealththerapycenter.bo;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl.*;

public class BOFactory {
    private static BOFactory boFactory;

    private BOFactory() {}

    public static BOFactory getInstance() {
        if (boFactory == null) {
            boFactory = new BOFactory();
        }
        return boFactory;
    }

    public enum BOType {
        USER, THERAPIST, THERAPY_PROGRAM, PATIENT, THERAPY_SESSION, PAYMENT
    }

    public <T> T getBO(BOType type) {
        switch (type) {
            case USER:
                return (T) new UserBOImpl();
            case THERAPIST:
                return (T) new TherapistBOImpl();
            case THERAPY_PROGRAM:
                return (T) new TherapyProgramBOImpl();
            case PATIENT:
                return (T) new PatientBOImpl();
            case THERAPY_SESSION:
                return (T) new TherapySessionBOImpl();
            case PAYMENT:
                return (T) new PaymentBOImpl();
            default:
                return null;
        }
    }
}
