package nl.avisi.numbered.headings;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.message.MessageCollection;

import org.apache.commons.lang.NotImplementedException;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public class KeyReturningI18NResolver implements I18nResolver {

    @Override
    public String getRawText(String key) {
        return key;
    }

    @Override
    public String getRawText(Locale locale, String key) {
        return key;
    }

    @Override
    public String getText(String key, Serializable... arguments) {
        return key;
    }

    @Override
    public String getText(Locale locale, String key, Serializable... arguments) {
        return key;
    }

    @Override
    public String getText(String key) {
        return key;
    }

    @Override
    public String getText(Locale locale, String key) {
        return key;
    }

    @Override
    public String getText(Message message) {
        return message.getKey();
    }

    @Override
    public String getText(Locale locale, Message message) {
        return message.getKey();
    }

    @Override
    public Message createMessage(String key, Serializable... arguments) {
        throw new NotImplementedException();
    }

    @Override
    public MessageCollection createMessageCollection() {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(String key) {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, String> getAllTranslationsForPrefix(String key, Locale locale) {
        throw new NotImplementedException();
    }
}
