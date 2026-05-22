package lk.ijse.theserenitymentalhealththerapycenter.util;


public class IdGenerator {


    public static String generateNextId(String prefix, String lastId) {
        if (lastId == null || lastId.isEmpty()) {
            return prefix + "001";
        }
        String numPart = lastId.substring(prefix.length());
        int nextNum = Integer.parseInt(numPart) + 1;
        return prefix + String.format("%03d", nextNum);
    }
}
