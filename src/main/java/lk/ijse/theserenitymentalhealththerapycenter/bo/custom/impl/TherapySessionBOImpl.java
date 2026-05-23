package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapySessionBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapySessionDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PatientDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapistDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapyProgramDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapySessionDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.*;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.PaymentRequiredException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.SchedulingConflictException;

import java.util.ArrayList;
import java.util.List;

public class TherapySessionBOImpl implements TherapySessionBO {

    private final TherapySessionDAO sessionDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_SESSION);
    private final PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);
    private final TherapistDAO therapistDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPIST);
    private final TherapyProgramDAO programDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);

    @Override
    public boolean saveSession(TherapySessionDTO dto) throws Exception {
        validateSession(dto);
        if (sessionDAO.checkConflict(dto.getTherapistId(), dto.getDate(), dto.getTime())) {
            throw new SchedulingConflictException("Therapist already has a session at " + dto.getDate() + " " + dto.getTime());
        }
        
        // POS Logic: canBookSession gatekeeper
        List<lk.ijse.theserenitymentalhealththerapycenter.entity.Payment> payments = paymentDAO.getPaymentsByPatient(dto.getPatientId());
        int totalCoveredSessions = 0;
        double totalPaid = 0;
        for (lk.ijse.theserenitymentalhealththerapycenter.entity.Payment p : payments) {
            if (p.getTherapyProgram().getProgramId().equals(dto.getProgramId())) {
                totalCoveredSessions += p.getCoveredSessions();
                totalPaid += p.getAmount();
            }
        }

        List<TherapySession> existingSessions = sessionDAO.getSessionsByPatient(dto.getPatientId());
        int bookedSessions = 0;
        for (TherapySession s : existingSessions) {
            if (s.getTherapyProgram().getProgramId().equals(dto.getProgramId()) && !s.getStatus().equals("CANCELLED")) {
                bookedSessions++;
            }
        }

        TherapyProgram program = programDAO.search(dto.getProgramId());
        double dueBalance = program.getFee() - totalPaid;

        if (bookedSessions >= totalCoveredSessions && dueBalance > 0) {
            throw new PaymentRequiredException("Payment Required: Patient has exhausted prepaid sessions for this program and has a pending balance.");
        } else if (bookedSessions >= totalCoveredSessions) {
            throw new SchedulingConflictException("Cannot book session: Patient has exhausted prepaid sessions for this program.");
        }

        Patient patient = patientDAO.search(dto.getPatientId());
        Therapist therapist = therapistDAO.search(dto.getTherapistId());
        return sessionDAO.save(new TherapySession(dto.getSessionId(), dto.getDate(), dto.getTime(), dto.getStatus(), patient, therapist, program));
    }

    @Override
    public boolean updateSession(TherapySessionDTO dto) throws Exception {
        validateSession(dto);
        Patient patient = patientDAO.search(dto.getPatientId());
        Therapist therapist = therapistDAO.search(dto.getTherapistId());
        TherapyProgram program = programDAO.search(dto.getProgramId());
        return sessionDAO.update(new TherapySession(dto.getSessionId(), dto.getDate(), dto.getTime(), dto.getStatus(), patient, therapist, program));
    }

    @Override
    public boolean deleteSession(String id) throws Exception {
        return sessionDAO.delete(id);
    }

    @Override
    public TherapySessionDTO searchSession(String id) throws Exception {
        TherapySession s = sessionDAO.search(id);
        if (s == null) return null;
        return toDTO(s);
    }

    @Override
    public List<TherapySessionDTO> getAllSessions() throws Exception {
        List<TherapySession> sessions = sessionDAO.getAll();
        List<TherapySessionDTO> dtos = new ArrayList<>();
        for (TherapySession s : sessions) dtos.add(toDTO(s));
        return dtos;
    }

    @Override
    public List<TherapySessionDTO> getSessionsByPatient(String patientId) throws Exception {
        List<TherapySession> sessions = sessionDAO.getSessionsByPatient(patientId);
        List<TherapySessionDTO> dtos = new ArrayList<>();
        for (TherapySession s : sessions) dtos.add(toDTO(s));
        return dtos;
    }

    private TherapySessionDTO toDTO(TherapySession s) {
        return new TherapySessionDTO(s.getSessionId(), s.getDate(), s.getTime(), s.getStatus(),
                s.getPatient().getId(), s.getPatient().getName(),
                s.getTherapist().getId(), s.getTherapist().getName(),
                s.getTherapyProgram().getProgramId(), s.getTherapyProgram().getName());
    }

    private void validateSession(TherapySessionDTO dto) {
        if (dto.getDate() == null || dto.getDate().trim().isEmpty())
            throw new InvalidInputException("Session date is required");
        if (dto.getTime() == null || dto.getTime().trim().isEmpty())
            throw new InvalidInputException("Session time is required");
        if (dto.getPatientId() == null) throw new InvalidInputException("Patient is required");
        if (dto.getTherapistId() == null) throw new InvalidInputException("Therapist is required");
    }

    @Override
    public String getNextId() throws Exception {
        String lastId = sessionDAO.getLastId();
        return lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("S", lastId);
    }
}
