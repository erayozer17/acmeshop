package com.erayoezer.acmeshop.service.email;

import com.erayoezer.acmeshop.model.email.EmailPayload;

public interface EmailSender {
    void sendEmail(EmailPayload payload);
}
