package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapistDTO;

import java.util.List;

public interface TherapistBO {
    boolean saveTherapist(TherapistDTO therapistDTO) throws Exception;
    boolean updateTherapist(TherapistDTO therapistDTO) throws Exception;
    boolean deleteTherapist(String id) throws Exception;
    TherapistDTO searchTherapist(String id) throws Exception;
    List<TherapistDTO> getAllTherapists() throws Exception;
}
