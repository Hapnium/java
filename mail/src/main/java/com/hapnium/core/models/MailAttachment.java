package com.hapnium.core.models;

import com.resend.services.emails.model.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an email attachment that can be included in a message.
 * This model wraps around Resend's {@link Attachment} object.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailAttachment {
    /**
     * The name of the file to be displayed in the email.
     */
    private String fileName;

    /**
     * The Base64-encoded content of the file.
     */
    private String content;

    /**
     * Optional path to the file on the local file system or resource path.
     */
    private String path;

    /**
     * Converts this object to Resend's {@link Attachment} model.
     *
     * @return a new {@link Attachment} object with the current parameters.
     */
    public Attachment getAttachment() {
        return Attachment.builder().fileName(fileName).content(content).path(path).build();
    }
}