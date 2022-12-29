package model;

public class FileOrResourceNotFoundException extends RuntimeException{
    public FileOrResourceNotFoundException(final String message) {
        super(message);
    }
}
