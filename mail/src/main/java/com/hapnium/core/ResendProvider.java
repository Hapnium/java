package com.hapnium.core;

import com.hapnium.core.exception.HapMailException;
import com.hapnium.core.models.MailRequest;
import com.hapnium.core.service.MailProviderService;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ResendProvider implements MailProviderService {
    private final Resend resend;

    ResendProvider(String apiKey, boolean showLog) {
        this.resend = new Resend(apiKey);

        if(showLog) {
            log.info("MAIL SDK INITIALIZER::: Resend Initialized for {}", this.resend);
            log.info("MAIL SDK INITIALIZER::: Resend Initialized with {}", this.resend.getClass().descriptorString());
        }
    }

    @Override
    public String send(MailRequest param) {
        try {
            CreateEmailResponse data = resend.emails().send(MailUtils.getOptions(param));
            return data.getId();
        } catch (ResendException e) {
            throw new HapMailException("Failed to send email: " + e.getMessage());
        }
    }
}