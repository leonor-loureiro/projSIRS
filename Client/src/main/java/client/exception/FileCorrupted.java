package client.exception;

public class FileCorrupted extends Exception{
    public FileCorrupted() {
    }

    public FileCorrupted(String message) {
        super(message);
    }
}
