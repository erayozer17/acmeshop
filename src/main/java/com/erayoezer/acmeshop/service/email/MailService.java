package com.erayoezer.acmeshop.service.email;

import com.erayoezer.acmeshop.model.email.EmailPayload;
import com.erayoezer.acmeshop.model.email.From;
import com.erayoezer.acmeshop.model.email.Recipient;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final EmailSender emailSender;
    private final MarkdownConverter markdownConverter;
    private final String fromEmail;

    @Autowired
    public MailService(@Value("${email.fromUser}") String fromEmail, EmailSender emailSender, MarkdownConverter markdownConverter) {
        this.emailSender = emailSender;
        this.markdownConverter = markdownConverter;
        this.fromEmail = fromEmail;
    }

    public void sendEmail(String toEmail, String subject, String content) {
        if (!isValidEmail(toEmail)) {
            logger.error("Invalid email address: {}", toEmail);
            return;
        }

        subject = sanitizeInput(subject);
        content = sanitizeInput(content);

        if (!content.trim().startsWith("<") && !content.contains("<html>")) {
            content = markdownConverter.convertToHtml(content);  // for non-html results
        }

        content = trimUntilHTMLTag(content); // for results like html<html> from AI response

        From from = new From(fromEmail);
        Recipient[] to = { new Recipient(toEmail) };
        EmailPayload payload = new EmailPayload(from, to, subject);
        payload.setHtml(content);

        emailSender.sendEmail(payload);
    }

    private boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[\r\n]", "");
    }

    private static String trimUntilHTMLTag(String str) {
        if (str.contains("<html>")) {
            int index = str.indexOf('<');
            if (index != -1) {
                return str.substring(index);
            }
        }
        return str;
    }
}
