package nl.avisi.numbered.headings.i18n;

import com.atlassian.sal.api.message.Message;

import javax.annotation.Nonnull;

import java.io.Serializable;

/**
 * A simple I18N message wrapper that contains the i18n key and arguments.
 */
public class I18nMessage implements Message {

    public static final String I18N_KEY_PREFIX = "nl.avisi.numbered-headings.numbered-headings";

    private final String key;
    private Serializable[] arguments = new Serializable[0];

    public I18nMessage(@Nonnull String key) {
        this.key = key;
    }

    public I18nMessage(@Nonnull String key, @Nonnull Serializable... arguments) {
        this.key = key;
        this.arguments = arguments;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Serializable[] getArguments() {
        return arguments.clone();
    }
}
