package client.exception;

public class BadArgument extends Exception {
    public BadArgument() {
    }

    public BadArgument(String message) {
        super(message);
    }
}
