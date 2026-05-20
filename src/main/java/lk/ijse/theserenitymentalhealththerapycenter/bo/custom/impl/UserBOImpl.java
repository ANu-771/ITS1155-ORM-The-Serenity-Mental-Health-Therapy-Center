package lk.ijse.theserenitymentalhealththerapycenter.bo.custom.impl;

import lk.ijse.theserenitymentalhealththerapycenter.bo.custom.UserBO;
import lk.ijse.theserenitymentalhealththerapycenter.dao.DAOFactory;
import lk.ijse.theserenitymentalhealththerapycenter.dao.custom.UserDAO;
import lk.ijse.theserenitymentalhealththerapycenter.dto.UserDTO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.User;
import lk.ijse.theserenitymentalhealththerapycenter.exception.AuthenticationException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.DuplicateEntryException;
import lk.ijse.theserenitymentalhealththerapycenter.exception.InvalidInputException;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.List;

public class UserBOImpl implements UserBO {

    private final UserDAO userDAO = DAOFactory.getInstance().getDAO(DAOFactory.DAOType.USER);

    @Override
    public boolean saveUser(UserDTO userDTO) throws Exception {
        // Validate inputs
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new InvalidInputException("Username is required");
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            throw new InvalidInputException("Password is required");
        }
        if (userDTO.getRole() == null || userDTO.getRole().trim().isEmpty()) {
            throw new InvalidInputException("Role is required");
        }

        // Check for duplicate username
        if (userDAO.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateEntryException("Username '" + userDTO.getUsername() + "' already exists");
        }

        // Hash the password with BCrypt before saving
        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());

        User user = new User(
                userDTO.getId(),
                userDTO.getUsername(),
                hashedPassword,
                userDTO.getRole()
        );
        return userDAO.save(user);
    }

    @Override
    public boolean updateUser(UserDTO userDTO) throws Exception {
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            throw new InvalidInputException("Username is required");
        }

        // Check if another user has the same username
        User existingUser = userDAO.findByUsername(userDTO.getUsername());
        if (existingUser != null && !existingUser.getId().equals(userDTO.getId())) {
            throw new DuplicateEntryException("Username '" + userDTO.getUsername() + "' already exists");
        }

        // If password is provided, hash it; otherwise keep existing
        String password;
        if (userDTO.getPassword() != null && !userDTO.getPassword().trim().isEmpty()) {
            password = BCrypt.hashpw(userDTO.getPassword(), BCrypt.gensalt());
        } else {
            User currentUser = userDAO.search(userDTO.getId());
            password = currentUser.getPassword();
        }

        User user = new User(
                userDTO.getId(),
                userDTO.getUsername(),
                password,
                userDTO.getRole()
        );
        return userDAO.update(user);
    }

    @Override
    public boolean deleteUser(String id) throws Exception {
        return userDAO.delete(id);
    }

    @Override
    public UserDTO searchUser(String id) throws Exception {
        User user = userDAO.search(id);
        if (user == null) return null;
        return new UserDTO(user.getId(), user.getUsername(), "", user.getRole());
    }

    @Override
    public List<UserDTO> getAllUsers() throws Exception {
        List<User> users = userDAO.getAll();
        List<UserDTO> userDTOs = new ArrayList<>();
        for (User user : users) {
            // Don't expose password in DTO
            userDTOs.add(new UserDTO(user.getId(), user.getUsername(), "", user.getRole()));
        }
        return userDTOs;
    }

    @Override
    public UserDTO authenticateUser(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password is required");
        }

        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new AuthenticationException("Invalid username or password");
        }

        // Verify password using BCrypt's checkpw method
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return new UserDTO(user.getId(), user.getUsername(), "", user.getRole());
    }

    @Override
    public void createDefaultAdmin() throws Exception {
        long count = userDAO.getUserCount();
        if (count == 0) {
            String hashedPassword = BCrypt.hashpw("admin123", BCrypt.gensalt());
            User admin = new User("U001", "admin", hashedPassword, "Admin");
            userDAO.save(admin);
            System.out.println("Default admin account created (username: admin, password: admin123)");
        }
    }
}
