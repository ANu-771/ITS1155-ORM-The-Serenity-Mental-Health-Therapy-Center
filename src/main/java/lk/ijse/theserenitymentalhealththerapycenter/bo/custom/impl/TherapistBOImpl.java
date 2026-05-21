package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapistBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapistDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapistDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.Therapist;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class TherapistBOImpl implements TherapistBO {

    private final TherapistDAO therapistDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPIST);

    @Override
    public boolean saveTherapist(TherapistDTO dto) throws Exception {
        validateTherapist(dto);
        Therapist therapist = new Therapist(dto.getId(), dto.getName(), dto.getSpecialization(), dto.getContactNumber(), dto.getEmail());
        return therapistDAO.save(therapist);
    }

    @Override
    public boolean updateTherapist(TherapistDTO dto) throws Exception {
        validateTherapist(dto);
        Therapist therapist = new Therapist(dto.getId(), dto.getName(), dto.getSpecialization(), dto.getContactNumber(), dto.getEmail());
        return therapistDAO.update(therapist);
    }

    @Override
    public boolean deleteTherapist(String id) throws Exception {
        return therapistDAO.delete(id);
    }

    @Override
    public TherapistDTO searchTherapist(String id) throws Exception {
        Therapist t = therapistDAO.search(id);
        if (t == null) return null;
        return new TherapistDTO(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail());
    }

    @Override
    public List<TherapistDTO> getAllTherapists() throws Exception {
        List<Therapist> therapists = therapistDAO.getAll();
        List<TherapistDTO> dtos = new ArrayList<>();
        for (Therapist t : therapists) {
            dtos.add(new TherapistDTO(t.getId(), t.getName(), t.getSpecialization(), t.getContactNumber(), t.getEmail()));
        }
        return dtos;
    }

    private void validateTherapist(TherapistDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Therapist name is required");
        }
        if (dto.getSpecialization() == null || dto.getSpecialization().trim().isEmpty()) {
            throw new InvalidInputException("Specialization is required");
        }
        // Strict email regex
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new InvalidInputException("Invalid email format (e.g. doctor@serenity.lk)");
            }
        }
        // Sri Lanka phone format: +94XXXXXXXXX or 0XXXXXXXXX
        if (dto.getContactNumber() != null && !dto.getContactNumber().trim().isEmpty()) {
            if (!dto.getContactNumber().matches("^(\\+94|0)\\d{9}$")) {
                throw new InvalidInputException("Invalid phone format (e.g. +94771234567 or 0771234567)");
            }
        }
    }

    @Override
    public String getNextId() throws Exception {
        String lastId = therapistDAO.getLastId();
        return lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("T", lastId);
    }
}
