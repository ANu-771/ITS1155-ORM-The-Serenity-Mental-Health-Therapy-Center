package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapyProgramBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapyProgramDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.TherapyProgram;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class TherapyProgramBOImpl implements TherapyProgramBO {
    private final TherapyProgramDAO programDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapySessionDAO sessionDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_SESSION);

    @Override
    public boolean saveProgram(TherapyProgramDTO dto) throws Exception {
        validateProgram(dto);
        TherapyProgram program = new TherapyProgram(dto.getProgramId(), dto.getName(), dto.getDuration(), dto.getFee(), dto.getTotalSessions(), dto.getDescription());
        return programDAO.save(program);
    }

    @Override
    public boolean updateProgram(TherapyProgramDTO dto) throws Exception {
        validateProgram(dto);
        TherapyProgram program = new TherapyProgram(dto.getProgramId(), dto.getName(), dto.getDuration(), dto.getFee(), dto.getTotalSessions(), dto.getDescription());
        return programDAO.update(program);
    }

    @Override
    public boolean deleteProgram(String id) throws Exception {
        return programDAO.delete(id);
    }

    @Override
    public TherapyProgramDTO searchProgram(String id) throws Exception {
        TherapyProgram p = programDAO.search(id);
        if (p == null) return null;
        return new TherapyProgramDTO(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription());
    }

    @Override
    public List<TherapyProgramDTO> getAllPrograms() throws Exception {
        List<TherapyProgram> programs = programDAO.getAll();
        List<TherapyProgramDTO> dtos = new ArrayList<>();
        for (TherapyProgram p : programs) {
            dtos.add(new TherapyProgramDTO(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription()));
        }
        return dtos;
    }

    @Override
    public List<TherapyProgramDTO> getProgramsByPatient(String patientId) throws Exception {
        List<TherapyProgramDTO> dtos = new ArrayList<>();
        List<TherapyProgram> programs = programDAO.getProgramsByPatient(patientId);
        for (TherapyProgram p : programs) {
            dtos.add(new TherapyProgramDTO(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getTotalSessions(), p.getDescription()));
        }
        return dtos;
    }

    @Override
    public List<TherapyProgramDTO> getEligibleProgramsForPatient(String patientId) throws Exception {
        List<TherapyProgramDTO> eligiblePrograms = new ArrayList<>();
        List<TherapyProgram> programs = programDAO.getProgramsByPatient(patientId);

        List<lk.ijse.theserenitymentalhealththerapycenter.entity.Payment> payments = paymentDAO.getPaymentsByPatient(patientId);
        List<lk.ijse.theserenitymentalhealththerapycenter.entity.TherapySession> sessions = sessionDAO.getSessionsByPatient(patientId);

        for (TherapyProgram program : programs) {
            int totalCoveredSessions = 0;
            for (lk.ijse.theserenitymentalhealththerapycenter.entity.Payment p : payments) {
                if (p.getTherapyProgram().getProgramId().equals(program.getProgramId())) {
                    totalCoveredSessions += p.getCoveredSessions();
                }
            }

            int bookedSessions = 0;
            for (lk.ijse.theserenitymentalhealththerapycenter.entity.TherapySession s : sessions) {
                if (s.getTherapyProgram().getProgramId().equals(program.getProgramId())) {
                    bookedSessions++;
                }
            }

            if (bookedSessions < totalCoveredSessions) {
                eligiblePrograms.add(new TherapyProgramDTO(program.getProgramId(), program.getName(), program.getDuration(), program.getFee(), program.getTotalSessions(), program.getDescription()));
            }
        }

        return eligiblePrograms;
    }

    private void validateProgram(TherapyProgramDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Program name is required");
        }
        if (dto.getDuration() == null || dto.getDuration().trim().isEmpty()) {
            throw new InvalidInputException("Duration is required");
        }
        if (dto.getTotalSessions() <= 0) {
            throw new InvalidInputException("Total Sessions must be greater than zero");
        }
        if (dto.getFee() <= 0) {
            throw new InvalidInputException("Fee must be greater than zero");
        }
    }

    @Override
    public String getNextId() throws Exception {
        String lastId = programDAO.getLastId();
        return lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("TP", lastId);
    }
}
