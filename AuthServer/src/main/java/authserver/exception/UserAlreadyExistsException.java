package authserver.exception;

public class UserAlreadyExistsException extends Exception {
    private String message = "User already exists";

    public UserAlreadyExistsException(String message) {
        this.message = message;
    }

    public UserAlreadyExistsException() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
