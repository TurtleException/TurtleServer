package de.eldritch.turtle.server.util.version;

public class IllegalVersionException extends RuntimeException {
    IllegalVersionException(String raw, Throwable cause) {
        super("'" + raw + "' could not be converted to Version", cause);
    }

    public IllegalVersionException(String message) {
        super(message);
    }
}