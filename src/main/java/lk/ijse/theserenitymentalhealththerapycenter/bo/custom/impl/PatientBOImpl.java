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
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.TherapyProgramDAO programDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.THERAPY_PROGRAM);
    private final lk.ijse.theserenitymentalhealththerapycenter.dao.custom.PaymentDAO paymentDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.PAYMENT);

    @Override
    public boolean savePatient(PatientDTO dto) throws Exception {
        validatePatient(dto);
        Patient patient = new Patient(dto.getId(), dto.getName(), dto.getDob(), dto.getContactNumber(), dto.getGender(), dto.getMedicalHistory(), dto.getRegistrationDate(), dto.isVerified());
        return patientDAO.save(patient);
    }

    @Override
    public boolean updatePatient(PatientDTO dto) throws Exception {
        validatePatient(dto);
        Patient patient = new Patient(dto.getId(), dto.getName(), dto.getDob(), dto.getContactNumber(), dto.getGender(), dto.getMedicalHistory(), dto.getRegistrationDate(), dto.isVerified());
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
        return new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getMedicalHistory(), p.getRegistrationDate(), p.isVerified());
    }

    @Override
    public List<PatientDTO> getAllPatients() throws Exception {
        List<Patient> patients = patientDAO.getAll();
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getMedicalHistory(), p.getRegistrationDate(), p.isVerified()));
        }
        return dtos;
    }

    @Override
    public List<PatientDTO> searchPatientsByName(String name) throws Exception {
        List<Patient> patients = patientDAO.searchByName(name);
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getMedicalHistory(), p.getRegistrationDate(), p.isVerified()));
        }
        return dtos;
    }

    @Override
    public List<PatientDTO> getPatientsWithPrograms() throws Exception {
        List<Patient> patients = patientDAO.getPatientsWithPrograms();
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getMedicalHistory(), p.getRegistrationDate(), p.isVerified()));
        }
        return dtos;
    }

    @Override
    public List<PatientDTO> getPatientsEnrolledInAllPrograms() throws Exception {
        List<Patient> patients = patientDAO.getPatientsEnrolledInAllPrograms();
        List<PatientDTO> dtos = new ArrayList<>();
        for (Patient p : patients) {
            dtos.add(new PatientDTO(p.getId(), p.getName(), p.getDob(), p.getContactNumber(), p.getGender(), p.getMedicalHistory(), p.getRegistrationDate(), p.isVerified()));
        }
        return dtos;
    }

    private void validatePatient(PatientDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new InvalidInputException("Patient name is required");
        }


        if (dto.getContactNumber() != null && !dto.getContactNumber().trim().isEmpty()) {
            if (!dto.getContactNumber().matches("^(\\+94|0)\\d{9}$")) {
                throw new InvalidInputException("Invalid phone format (e.g. +94771234567 or 0771234567)");
            }
        }
    }

    @Override
    public String getNextId() throws Exception {
        String lastId = patientDAO.getLastId();
        return lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("P", lastId);
    }

    public boolean registerPatient(PatientDTO dto, List<String> programIds, double upfrontPayment, String paymentMethod) throws Exception {
        validatePatient(dto);
        if (programIds == null || programIds.isEmpty()) throw new InvalidInputException("At least one program must be selected for registration");

        Patient patient = new Patient(dto.getId(), dto.getName(), dto.getDob(), dto.getContactNumber(), dto.getGender(), dto.getMedicalHistory(), dto.getRegistrationDate(), dto.isVerified());

        List<lk.ijse.theserenitymentalhealththerapycenter.entity.Payment> payments = new ArrayList<>();
        String currentPaymentId = paymentDAO.getLastId();

        double remainingPayment = upfrontPayment;

        for (String programId : programIds) {
            lk.ijse.theserenitymentalhealththerapycenter.entity.TherapyProgram program = programDAO.search(programId);
            if (program == null) throw new Exception("Therapy Program not found");

            double fee = program.getFee();
            double appliedAmount = 0.0;

            if (remainingPayment >= fee) {
                appliedAmount = fee;
                remainingPayment -= fee;
            } else if (remainingPayment > 0) {
                appliedAmount = remainingPayment;
                remainingPayment = 0;
            }

            int coveredSessions = 0;
            double dueBalance = fee - appliedAmount;

            if (program.getTotalSessions() > 0) {
                double perSessionRate = fee / program.getTotalSessions();
                coveredSessions = (int) Math.floor(appliedAmount / perSessionRate);
            }

            currentPaymentId = lk.ijse.theserenitymentalhealththerapycenter.util.IdGenerator.generateNextId("PAY", currentPaymentId);
            
            lk.ijse.theserenitymentalhealththerapycenter.entity.Payment payment = new lk.ijse.theserenitymentalhealththerapycenter.entity.Payment(
                    currentPaymentId, appliedAmount, java.time.LocalDate.now().toString(), paymentMethod, 
                    dueBalance > 0 ? "PARTIAL" : "COMPLETED", coveredSessions, dueBalance, patient, program
            );
            payments.add(payment);
        }

        return patientDAO.registerPatient(patient, programIds, payments);
    }
}
