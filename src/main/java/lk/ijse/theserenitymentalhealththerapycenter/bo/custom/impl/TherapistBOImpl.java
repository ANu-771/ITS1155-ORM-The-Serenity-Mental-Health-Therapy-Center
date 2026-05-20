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
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new InvalidInputException("Invalid email format");
            }
        }
    }
}
