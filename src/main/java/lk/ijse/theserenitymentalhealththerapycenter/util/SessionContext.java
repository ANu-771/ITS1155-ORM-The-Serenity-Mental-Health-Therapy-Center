package lk.ijse.theserenitymentalhealththerapycenter.util;

import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;

/**
 * Holds the currently logged-in user's info so that
 * any screen (e.g. Change Credentials) can access it.
 */
public class SessionContext {

    private static UserDTO currentUser;

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserDTO user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }
}
