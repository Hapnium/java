package com.hapnium.core.models;

import com.hapnium.core.enums.MailProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration parameters required for sending emails using SMTP or an email service provider.
 */
@Data
@Builder
@AllArgsConstructor
public class MailParam {
    /**
     * SMTP server host (e.g., smtp.gmail.com).
     */
    private String host;

    /**
     * SMTP server port (e.g., 587 for TLS, 465 for SSL).
     */
    private Integer port;

    /**
     * Username for SMTP authentication.
     */
    private String username;

    /**
     * Password for SMTP authentication.
     */
    private String password;

    /**
     * Email sending protocol (e.g., "smtp").
     */
    private String protocol = "smtp";

    /**
     * Display name for the email sender.
     */
    private String senderName;

    /**
     * Sender email address (e.g., no-reply@example.com).
     */
    private String senderEmail;

    /**
     * Path to the directory containing email templates.
     * <p></p>
     * The path to your template directory, e.g., {@code "/templates/"}.
     */
    private String templateDirectory;

    /**
     * API key for third-party email service integration (optional if not using SMTP directly).
     */
    private String apiKey;

    /**
     * The provider for the email configuration
     */
    private MailProvider provider = MailProvider.RESEND;

    /**
     * Whether to show logs
     */
    private Boolean showLog;

    public boolean getShowLog() {
        if(showLog == null) {
            return false;
        }

        return showLog;
    }

    public MailProvider getProvider() {
        if(provider == null) {
            return MailProvider.RESEND;
        }

        return provider;
    }

    public MailParam() {}

    public static @NotNull MailParam resend(String apiKey, String templateDirectory) {
        MailParam param = new MailParam();
        param.setApiKey(apiKey);
        param.setTemplateDirectory(templateDirectory);

        return param;
    }
}