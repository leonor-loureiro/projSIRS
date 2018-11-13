package client;

import java.security.Key;

public class User {

    private String username;
    private char[] password;
    private Key privateKey;
    private Key publicKey;

    public User() {
    }

    public User(String username, char[] password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, char[] password, Key privateKey, Key publicKey) {
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public Key getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(Key privateKey) {
        this.privateKey = privateKey;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Key getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(Key publicKey) {
        this.publicKey = publicKey;
    }
}
