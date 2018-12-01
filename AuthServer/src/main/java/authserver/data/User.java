package authserver.data;

/**
 * This class represents a user
 */
public class User {
    private String username;
    private String  saltedPwd;
    private String Kpub;

    public User(String username, String saltedPwd, String Kpub) {
        this.username = username;
        this.saltedPwd = saltedPwd;
        this.Kpub = Kpub;
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

    public String getKpub() {
        return Kpub;
    }

    public void setKpub(String kpub) {
        Kpub = kpub;
    }
}
