package nl.avisi.numbered.headings.exception;

/**
 * Use this class only for technical exceptions that should not be shown to the user (the user will get a generic error message).
 */
public class NumberedHeadingsException extends RuntimeException {

    public NumberedHeadingsException(final String message) {
        super(message);
    }
}
