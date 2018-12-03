package client.security.exception;

public class BadEncryption extends Exception {

    public BadEncryption() {
    }

    public BadEncryption(String message) {
        super(message);
    }

}
