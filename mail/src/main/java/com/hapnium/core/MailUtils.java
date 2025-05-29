package com.hapnium.core;

import com.hapnium.core.enums.MailType;
import com.hapnium.core.models.MailAttachment;
import com.hapnium.core.models.MailRequest;
import com.hapnium.core.models.MailTag;
import com.resend.services.emails.model.CreateEmailOptions;
import org.jetbrains.annotations.NotNull;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.Objects;

class MailUtils {
    public static @NotNull CreateEmailOptions getOptions(@NotNull MailRequest param) {
        CreateEmailOptions.Builder builder = CreateEmailOptions.builder();
        builder.from(param.getFrom());
        builder.to(param.getTo());
        builder.subject(param.getSubject());

        if (param.getReplyTo() != null) {
            builder.replyTo(param.getReplyTo());
        }

        if (param.getBcc() != null) {
            builder.bcc(param.getBcc());
        }

        if (param.getCc() != null) {
            builder.cc(param.getCc());
        }

        if (param.getHeaders() != null) {
            builder.headers(param.getHeaders());
        }

        if (param.getScheduledAt() != null && !param.getScheduledAt().isEmpty()) {
            builder.scheduledAt(param.getScheduledAt());
        }

        if (param.getTags() != null) {
            builder.tags(param.getTags().stream().map(MailTag::getTag).toList());
        }

        if (param.getAttachments() != null) {
            builder.attachments(param.getAttachments().stream().map(MailAttachment::getAttachment).toList());
        }

        if (Objects.requireNonNull(param.getType()) == MailType.HTML) {
            builder.html(param.getBody());
        } else {
            builder.text(param.getBody());
        }

        return builder.build();
    }

    public static @NotNull ClassLoaderTemplateResolver getResolver(String templateDirectory) {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(templateDirectory);
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);

        return resolver;
    }
}