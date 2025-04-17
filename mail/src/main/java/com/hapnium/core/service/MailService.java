package com.hapnium.core.service;

import java.util.Map;

/**
 * MailService defines the core email operations supported by any mail provider implementation.
 */
public interface MailService extends MailProviderService {
    /**
     * Converts a Thymeleaf HTML template to a rendered string using dynamic values.
     *
     * @param template The name of the template (without file extension).
     * @param params   A map of variables to inject into the template.
     * @return A fully rendered HTML string.
     */
    String convertHtmlToString(String template, Map<String, Object> params);
}