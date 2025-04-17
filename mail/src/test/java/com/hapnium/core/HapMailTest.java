package com.hapnium.core;

import com.hapnium.core.models.MailParam;
import com.hapnium.core.models.MailRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HapMailTest {
    private static final String API_KEY = "re_fosBf8AF_AhZWJZ5CVyV2gbSnoS32Rp9b";
    private static final String TEST_EMAIL = "evaristusadimonyemma@gmail.com";

    private MailParam param() {
        MailParam param = new MailParam();
        param.setApiKey(API_KEY);

        return param;
    }

    @Test
    void testSendTextEmail() {
        HapMail mail = new HapMail(param());

        MailRequest param = MailRequest.text(
                "Serch <noreply@notify.serchservice.com>",
                TEST_EMAIL,
                "Testing Plain Text Mail",
                "This is a plain text email sent via HapniumMail."
        );

        String response = mail.send(param);

        assertNotNull(response);
        System.out.println("Text Email Sent with ID: " + response);
    }

    @Test
    void testConvertHtmlAndSend() {
        MailParam param = param();
        param.setTemplateDirectory("/templates/");
        HapMail mail = new HapMail(param);

        String html = mail.convertHtmlToString("welcome", Map.of(
                "name", "Evaristus",
                "date", "2025-04-13"
        ));

        assertNotNull(html);
        assertTrue(html.contains("Evaristus"));

        MailRequest request = MailRequest.html(
                "Serch <noreply@notify.serchservice.com>",
                TEST_EMAIL,
                "HTML Email Test from Hapnium",
                html
        );

        String response = mail.send(request);

        assertNotNull(response);
        System.out.println("HTML Email Sent with ID: " + response);
    }

    @Test
    void testFailWhenParamsAreNull() {
        HapMail mail = HapMail.create(param());

        Exception exception = assertThrows(RuntimeException.class, () -> mail.send(null));
        assertTrue(exception.getMessage().contains("MailParam must not be null"));
    }
}