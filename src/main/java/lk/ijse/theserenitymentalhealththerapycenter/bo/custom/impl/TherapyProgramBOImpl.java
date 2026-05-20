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

    @Override
    public boolean saveProgram(TherapyProgramDTO dto) throws Exception {
        validateProgram(dto);
        TherapyProgram program = new TherapyProgram(dto.getProgramId(), dto.getName(), dto.getDuration(), dto.getFee(), dto.getDescription());
        return programDAO.save(program);
    }

    @Override
    public boolean updateProgram(TherapyProgramDTO dto) throws Exception {
        validateProgram(dto);
        TherapyProgram program = new TherapyProgram(dto.getProgramId(), dto.getName(), dto.getDuration(), dto.getFee(), dto.getDescription());
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
        return new TherapyProgramDTO(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getDescription());
    }

    @Override
    public List<TherapyProgramDTO> getAllPrograms() throws Exception {
        List<TherapyProgram> programs = programDAO.getAll();
        List<TherapyProgramDTO> dtos = new ArrayList<>();
        for (TherapyProgram p : programs) {
            dtos.add(new TherapyProgramDTO(p.getProgramId(), p.getName(), p.getDuration(), p.getFee(), p.getDescription()));
        }
        return dtos;
    }

    private void validateProgram(TherapyProgramDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Program name is required");
        }
        if (dto.getDuration() == null || dto.getDuration().trim().isEmpty()) {
            throw new InvalidInputException("Duration is required");
        }
        if (dto.getFee() <= 0) {
            throw new InvalidInputException("Fee must be greater than zero");
        }
    }
}
