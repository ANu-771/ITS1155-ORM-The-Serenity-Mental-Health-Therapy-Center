package lk.ijse.theserenitymentalhealththerapycenter.bo.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;

import java.util.List;

public interface UserBO {
    boolean saveUser(UserDTO userDTO) throws Exception;

    boolean updateUser(UserDTO userDTO) throws Exception;

    boolean deleteUser(String id) throws Exception;

    UserDTO searchUser(String id) throws Exception;

    List<UserDTO> getAllUsers() throws Exception;

    UserDTO authenticateUser(String username, String password) throws Exception;

    void createDefaultAdmin() throws Exception;

    String getNextId() throws Exception;

    boolean changeCredentials(String userId, String currentPassword, String newUsername, String newPassword) throws Exception;
}
