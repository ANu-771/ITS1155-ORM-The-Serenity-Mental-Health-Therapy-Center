package lk.ijse.theserenitymentalhealththerapycenter.util;

/**
 * Generates the next sequential ID based on a prefix and the last known ID.
 * Example: prefix "P", lastId "P005" → returns "P006"
 *          prefix "P", lastId null   → returns "P001"
 */
public class IdGenerator {

    /**
     * Generate next ID from a prefix and the current max ID.
     * @param prefix  e.g. "U", "P", "T", "TP", "S", "PAY"
     * @param lastId  the current maximum ID from the database (can be null)
     * @return next sequential ID like "P001", "P002", etc.
     */
    public static String generateNextId(String prefix, String lastId) {
        if (lastId == null || lastId.isEmpty()) {
            return prefix + "001";
        }
        // Extract numeric part after the prefix
        String numPart = lastId.substring(prefix.length());
        int nextNum = Integer.parseInt(numPart) + 1;
        return prefix + String.format("%03d", nextNum);
    }
}
