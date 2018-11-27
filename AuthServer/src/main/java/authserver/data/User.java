package authserver.data;

/**
 * This class represents a user
 */
public class User {
    private String username;
    private String  saltedPwd;

    public User(String username, String saltedPwd) {
        this.username = username;
        this.saltedPwd = saltedPwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSaltedPwd() {
        return saltedPwd;
    }

    public void setSaltedPwd(String saltedPwd) {
        this.saltedPwd = saltedPwd;
    }
}
