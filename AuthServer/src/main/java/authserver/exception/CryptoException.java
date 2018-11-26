package authserver.exception;

public class CryptoException extends Throwable {
    private String message;

    public CryptoException( String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
