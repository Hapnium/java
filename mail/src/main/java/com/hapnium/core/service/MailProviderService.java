package com.hapnium.core.service;

import com.hapnium.core.models.MailRequest;

public interface MailProviderService {
    /**
     * Sends an email with the provided parameters.
     *
     * @param param A MailParam object containing email details like to, from, body, etc.
     * @return The provider's ID of the sent email (used for tracking).
     */
    String send(MailRequest param);
}
