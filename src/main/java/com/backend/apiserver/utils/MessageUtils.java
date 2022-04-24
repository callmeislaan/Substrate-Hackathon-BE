package com.backend.apiserver.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Properties;

public class MessageUtils {

    /**
     * Message
     */
    private static Properties messages = new Properties();

    private static final String RESPONSE_RESOURCE_NAME = "responseMessages.properties";

    static {
        load(RESPONSE_RESOURCE_NAME);
    }

    private static void load(String location) {
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)) {
            if (in != null) {
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                messages.load(reader);
            }
        } catch (Exception e) {
            throw new RuntimeException(location + " loading failed", e);
        }
    }


    public static String getMessage(final String messageId, final Object... params) {
        if (params != null && params.length > 0) {
            return MessageFormat.format(messages.getProperty(messageId), params);
        } else {
            return messages.getProperty(messageId);
        }
    }
}
