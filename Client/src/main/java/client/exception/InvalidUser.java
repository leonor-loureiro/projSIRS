package client.exception;

public class InvalidUser extends Exception{
    public InvalidUser() {
    }

    public InvalidUser(String message) {
        super(message);
    }
}
