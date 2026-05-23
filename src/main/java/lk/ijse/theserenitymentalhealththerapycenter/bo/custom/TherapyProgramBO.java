package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapyProgramDTO;

import java.util.List;

public interface TherapyProgramBO {
    boolean saveProgram(TherapyProgramDTO programDTO) throws Exception;

    boolean updateProgram(TherapyProgramDTO programDTO) throws Exception;

    boolean deleteProgram(String id) throws Exception;

    TherapyProgramDTO searchProgram(String id) throws Exception;

    List<TherapyProgramDTO> getAllPrograms() throws Exception;

    List<TherapyProgramDTO> getProgramsByPatient(String patientId) throws Exception;

    List<TherapyProgramDTO> getEligibleProgramsForPatient(String patientId) throws Exception;

    String getNextId() throws Exception;
}
