package com.hapnium.core.models;

import com.hapnium.core.enums.MailType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Represents the parameters required to send an email via the mail service.
 * <p>
 * Includes sender and recipient information, subject, content,
 * optional CC, BCC, attachments, and metadata.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    /**
     * The email address of the sender. Must follow the format "Name <email@domain.com>".
     */
    private String from;

    /**
     * The primary recipient's email address.
     */
    private String to;

    /**
     * The subject of the email.
     */
    private String subject;

    /**
     * The content of the email. Can be either plain text or HTML depending on {@link #type}.
     */
    private String body;

    /**
     * List of email addresses to CC.
     */
    private List<String> cc;

    /**
     * List of email addresses to BCC.
     */
    private List<String> bcc;

    /**
     * List of email addresses for the "Reply-To" header.
     */
    private List<String> replyTo;

    /**
     * Optional headers to be added to the email (e.g., custom tracking headers).
     */
    private Map<String, String> headers;

    /**
     * List of attachments to include in the email.
     */
    private List<MailAttachment> attachments;

    /**
     * Tags used for categorization or tracking, represented as key-value pairs.
     */
    private List<MailTag> tags;

    /**
     * The scheduled send time for the email in ISO 8601 format.
     */
    private String scheduledAt;

    /**
     * The type of email content: {@link MailType#HTML} or {@link MailType#TEXT}.
     * Defaults to HTML.
     */
    private MailType type = MailType.HTML;

    /**
     * Convenience method to quickly create an HTML email.
     *
     * @param from    sender email
     * @param to      recipient email
     * @param subject email subject
     * @param body    HTML body
     * @return a {@link MailRequest} instance
     */
    public static MailRequest html(String from, String to, String subject, String body) {
        MailRequest mailRequest = getParam(from, to, subject, body);
        mailRequest.setType(MailType.HTML);
        return mailRequest;
    }

    /**
     * Convenience method to quickly create a plain text email.
     *
     * @param from    sender email
     * @param to      recipient email
     * @param subject email subject
     * @param body    plain text body
     * @return a {@link MailRequest} instance
     */
    public static MailRequest text(String from, String to, String subject, String body) {
        MailRequest mailRequest = getParam(from, to, subject, body);
        mailRequest.setType(MailType.TEXT);
        return mailRequest;
    }

    /**
     * Internal method to populate the common fields.
     */
    private static MailRequest getParam(String from, String to, String subject, String body) {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setFrom(from);
        mailRequest.setTo(to);
        mailRequest.setSubject(subject);
        mailRequest.setBody(body);
        return mailRequest;
    }
}