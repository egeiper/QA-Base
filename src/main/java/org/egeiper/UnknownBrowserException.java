package org.egeiper;
@SuppressWarnings("PMD.MissingSerialVersionUID")

public class UnknownBrowserException extends RuntimeException {
    public UnknownBrowserException(final String message) {
        super(message);
    }
}
