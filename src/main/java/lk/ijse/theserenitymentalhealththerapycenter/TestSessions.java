package lk.ijse.theserenitymentalhealththerapycenter;

import lk.ijse.theserenitymentalhealththerapycenter.bo.BOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.TherapySessionBO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.TherapySessionDTO;
import java.util.List;

public class TestSessions {
    public static void main(String[] args) {
        try {
            TherapySessionBO sessionBO = BOFactory.getInstance().getBO(BOFactory.BOType.THERAPY_SESSION);
            List<TherapySessionDTO> all = sessionBO.getAllSessions();
            System.out.println("Total sessions found: " + all.size());
            for (TherapySessionDTO s : all) {
                System.out.println(s.getSessionId() + " - " + s.getPatientName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
