package authserver.exception;

public class InvalidUserException extends Exception {
    private String message;

    public InvalidUserException(String message) {
        this.message = message;
    }

    public InvalidUserException() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
