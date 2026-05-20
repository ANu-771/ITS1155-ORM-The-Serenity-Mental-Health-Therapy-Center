package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.PatientBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PatientDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.PatientDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.Patient;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;

import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl implements PatientBO {

    private final PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PATIENT);

    @Override
    public boolean savePatient(PatientDTO dto) throws Exception {
        validatePatient(dto);
        Patient patient = new Patient(dto.getId(), dto.getName(), dto.getDob(), dto.getContactNumber(), dto.getEmail(), dto.getMedicalHistory(), dto.getRegistrationDate());
        return patientDAO.save(patient);
    }

    @Override
    public boolean updatePatient(PatientDTO dto) throws Exception {
        validatePatient(dto);
        Patient patient = new Patient(dto.getId(), dto.getName(), dto.getDob(), dto.getContactNumber(), dto.getEmail(), dto.getMedicalHistory(), dto.getRegistrationDate());
        return patientDAO.update(patient);
    }

    @Override
    public boolean deletePatient(String id) throws Exception {
        return patientDAO.delete(id);
    }

    @Override
    public PatientDTO searchPatient(String id) throws Exception {
        Patient p = patientDAO.search(id);
        if (p == null) return null;
        return new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getMedicalHistory(), p.getRegistrationDate());
    }

    @Override
    public List<PatientDTO> getAllPatients() throws Exception {
        List<Patient> patients = patientDAO.getAll();
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getMedicalHistory(), p.getRegistrationDate()));
        }
        return dtos;
    }

    @Override
    public List<PatientDTO> searchPatientsByName(String name) throws Exception {
        List<Patient> patients = patientDAO.searchByName(name);
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getEmail(), p.getMedicalHistory(), p.getRegistrationDate()));
        }
        return dtos;
    }

    private void validatePatient(PatientDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Patient name is required");
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!dto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new InvalidInputException("Invalid email format");
            }
        }
        if (dto.getContactNumber() != null && !dto.getContactNumber().trim().isEmpty()) {
            if (!dto.getContactNumber().matches("^[0-9+\\-\\s]{7,15}$")) {
                throw new InvalidInputException("Invalid phone number format");
            }
        }
    }
}
