package com.hapnium.core;

import com.hapnium.core.models.MailParam;
import com.hapnium.core.models.MailRequest;
import com.hapnium.core.service.MailService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * HapMail is a simple and user-friendly entry point for sending emails via the Resend API.
 * It implements the {@link MailService} interface and internally delegates logic to a private {@code Mailer} instance,
 * keeping implementation details hidden from the end user.
 *
 * <p>This class is ideal for developers who want to send transactional or templated emails
 * without interacting directly with the lower-level implementation.</p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 *   HapMail mail = HapMail.create("/templates/", "your-resend-api-key");
 *   String html = mail.convertHtmlToString("welcome", Map.of("name", "Jane"));
 *   mail.send(MailParam.html("from@example.com", "to@example.com", "Welcome!", html));
 * }</pre>
 * </p>
 */
public class HapMail implements MailService {
    private final MailService delegate;

    /**
     * Private constructor that wraps the internal {@link MailService} implementation.
     *
     * @param param The {@link MailParam} where email provider's configuration lies.
     */
    public HapMail(MailParam param) {
        this.delegate = new Mailer(param);
    }

    /**
     * Factory method to create a new instance of {@code HapMail}.
     *
     * @param param The {@link MailParam} where email provider's configuration lies.
     * @return a new instance of {@code HapMail}.
     */
    @Contract("_ -> new")
    public static @NotNull HapMail create(MailParam param) {
        return new HapMail(param);
    }

    @Override
    public String convertHtmlToString(String template, Map<String, Object> params) {
        return delegate.convertHtmlToString(template, params);
    }

    @Override
    public String send(MailRequest param) {
        return delegate.send(param);
    }
}