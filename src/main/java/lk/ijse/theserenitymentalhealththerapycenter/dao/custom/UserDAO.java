package lk.ijse.theserenitymentalhealththerapycenter.dao.custom;

import lk.ijse.theserenitymentalhealththerapycenter.dao.CrudDAO;
import lk.ijse.theserenitymentalhealththerapycenter.entity.User;

public interface UserDAO extends CrudDAO<User, String> {
    User findByUsername(String username) throws Exception;
    boolean existsByUsername(String username) throws Exception;
    long getUserCount() throws Exception;
}
