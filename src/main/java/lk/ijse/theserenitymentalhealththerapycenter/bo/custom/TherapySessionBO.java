package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapySessionDTO;

import java.util.List;

public interface TherapySessionBO {
    boolean saveSession(TherapySessionDTO sessionDTO) throws Exception;

    boolean updateSession(TherapySessionDTO sessionDTO) throws Exception;

    boolean deleteSession(String id) throws Exception;

    TherapySessionDTO searchSession(String id) throws Exception;

    List<TherapySessionDTO> getAllSessions() throws Exception;

    List<TherapySessionDTO> getSessionsByPatient(String patientId) throws Exception;

    String getNextId() throws Exception;
}
