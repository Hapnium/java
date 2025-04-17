package com.hapnium.core.models;

import com.resend.services.emails.model.Tag;
import lombok.Data;

/**
 * Represents a tag that can be associated with an email for categorization or tracking purposes.
 * <p>
 * Tags can help with analytics, filtering, or identifying emails in the sending provider.
 */
@Data
public class MailTag {
    /**
     * The name of the tag (e.g., "user-signup").
     */
    private String name;

    /**
     * The value associated with the tag (e.g., "newsletter").
     */
    private String value;

    /**
     * Converts this object into a Resend {@link Tag} object.
     *
     * @return a Resend-compatible tag.
     */
    public Tag getTag() {
        return Tag.builder().name(name).value(value).build();
    }
}