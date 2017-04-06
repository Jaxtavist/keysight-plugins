package nl.avisi.numbered.headings.exception;

import nl.avisi.numbered.headings.i18n.I18nMessage;

/**
 * Exception class specifically for showing to the end user. Therefor this class can only be constructed with an {@link I18nMessage}.
 */
public class NumberedHeadingsMacroException extends NumberedHeadingsException {

    private final I18nMessage i18nMessage;

    public NumberedHeadingsMacroException(final I18nMessage i18nMessage) {
        super(i18nMessage.getKey());

        this.i18nMessage = i18nMessage;
    }

    public final I18nMessage getI18nMessage() {
        return i18nMessage;
    }
}
