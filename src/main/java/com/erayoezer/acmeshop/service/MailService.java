package com.erayoezer.acmeshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final String fromEmail;
    private final String password;

    @Autowired
    public MailService(@Value("${email.username}") String fromEmail,
                       @Value("${email.password}") String password) {
        this.fromEmail = fromEmail;
        this.password = stripQuotes(password);
    }

    public void sendEmail(String toEmail, String subject, String content) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(prop,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(fromEmail, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            logger.info("Email is sent to {}", toEmail);
        } catch (MessagingException e) {
            logger.error("Email could not be sent. Error: {}", e.getMessage());
        }
    }

    private static String stripQuotes(String input) {
        if (input == null || input.length() < 2) {
            return input;
        }
        char firstChar = input.charAt(0);
        char lastChar = input.charAt(input.length() - 1);
        if ((firstChar == '"' && lastChar == '"') || (firstChar == '\'' && lastChar == '\'')) {
            return input.substring(1, input.length() - 1);
        }
        return input;
    }
}
