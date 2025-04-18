package com.hapnium.core;

import com.hapnium.core.exception.HapMailException;
import com.hapnium.core.models.MailParam;
import com.hapnium.core.models.MailRequest;
import com.hapnium.core.service.MailProviderService;
import com.hapnium.core.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * HapniumMail is the core implementation of the MailService interface.
 * It integrates with Resend API to send transactional or bulk emails.
 * It also supports Thymeleaf templating for dynamic HTML content rendering.
 * <p>
 * Usage:
 * <pre>{@code
 *   HapniumMail mail = new HapniumMail("/templates/", "your-resend-api-key");
 *   String html = mail.convertHtmlToString("welcome", Map.of("name", "Jane"));
 *   mail.send(new MailParam(...));
 * }</pre>
 */
@Slf4j
class Mailer implements MailService {
    private final TemplateEngine engine;
    private final MailProviderService service;

    /**
     * Constructs a HapniumMail instance with specified template directory and Resend API key.
     *
     * @param param The {@link MailParam} where email provider's configuration lies.
     * @throws HapMailException if both templateDirectory and apiKey are null or empty.
     */
    Mailer(@NotNull MailParam param) {
        if (param.getTemplateDirectory() != null && !param.getTemplateDirectory().isEmpty()) {
            TemplateEngine engine = new TemplateEngine();
            engine.setTemplateResolver(MailUtils.getResolver(param.getTemplateDirectory()));
            this.engine = engine;
        } else {
            this.engine = new TemplateEngine(); // Dummy fallback if templates aren't needed
        }

        if (param.getApiKey() == null || param.getApiKey().isEmpty()) {
            throw new HapMailException("Initialization failed: Api key must be provided.");
        }

        service = new ResendProvider(param.getApiKey(), param.getShowLog());
    }

    /**
     * Renders a Thymeleaf HTML template into a string using provided parameters.
     *
     * @param template The name of the template file (without ".html" suffix).
     * @param params   A map of variables to inject into the template.
     * @return The rendered HTML content as a string.
     * @throws HapMailException if rendering fails due to missing template or context errors.
     */
    @Override
    public String convertHtmlToString(String template, @NotNull Map<String, Object> params) {
        Context context = new Context();
        params.forEach(context::setVariable);

        try {
            return engine.process(template, context);
        } catch (Exception e) {
            throw new HapMailException("Failed to render template '" + template + "': " + e.getMessage());
        }
    }

    /**
     * Sends an email using the Resend API.
     *
     * @param param The mail parameters including to, from, subject, body, etc.
     * @return The ID of the email sent.
     * @throws HapMailException if sending fails (e.g. due to network or API error).
     */
    @Override
    public String send(MailRequest param) {
        if (param == null) {
            throw new HapMailException("Email sending failed: MailParam must not be null.");
        }

        return service.send(param);
    }
}