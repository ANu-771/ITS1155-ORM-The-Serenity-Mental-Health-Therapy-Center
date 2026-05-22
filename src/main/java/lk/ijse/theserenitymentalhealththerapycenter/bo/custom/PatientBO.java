package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.PatientDTO;

import java.util.List;

public interface PatientBO {
    boolean savePatient(PatientDTO patientDTO) throws Exception;

    boolean updatePatient(PatientDTO patientDTO) throws Exception;

    boolean deletePatient(String id) throws Exception;

    PatientDTO searchPatient(String id) throws Exception;

    List<PatientDTO> getAllPatients() throws Exception;

    List<PatientDTO> searchPatientsByName(String name) throws Exception;

    List<PatientDTO> getPatientsWithPrograms() throws Exception;

    List<PatientDTO> getPatientsEnrolledInAllPrograms() throws Exception;

    String getNextId() throws Exception;
    
    boolean registerPatient(PatientDTO patientDTO, String programId, String paymentMethod, double amount, String paymentId) throws Exception;
}
