package lk.ijse.theserenitymentalhealththerapycenter.dao.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dao.CrudDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.TherapySession;

import java.util.List;

public interface TherapySessionDAO extends CrudDAO<TherapySession, String> {
    List<TherapySession> getSessionsByTherapist(String therapistId) throws Exception;
    List<TherapySession> getSessionsByPatient(String patientId) throws Exception;
    boolean checkConflict(String therapistId, String date, String time) throws Exception;
    String getLastId() throws Exception;
}
