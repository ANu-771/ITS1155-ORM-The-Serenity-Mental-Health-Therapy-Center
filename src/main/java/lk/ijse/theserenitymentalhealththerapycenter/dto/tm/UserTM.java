package lk.ijse.theserenitymentalhealththerapycenter.dto.tm;

public class UserTM {
    private String id;
    private String username;
    private String role;
    private String email;

    public UserTM() {
    }

    public UserTM(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public UserTM(String id, String username, String role, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
