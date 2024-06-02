package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.email.EmailPayload;
import com.erayoezer.acmeshop.model.email.From;
import com.erayoezer.acmeshop.model.email.Recipient;
import com.google.gson.Gson;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private static final String API_URL = "https://api.mailersend.com/v1/email";

    private final String fromEmail;
    private final String token;

    @Autowired
    public MailService(@Value("${email.fromUser}") String fromEmail,
                       @Value("${email.token}") String token) {
        this.fromEmail = fromEmail;
        this.token = token;
    }

    public void sendEmail(String toEmail, String subject, String content) {
        if (!isValidEmail(toEmail)) {
            logger.error("Invalid email address: {}", toEmail);
            return;
        }

        subject = sanitizeInput(subject);
        content = sanitizeInput(content);

        try {
            From from = new From(fromEmail);
            Recipient[] to = { new Recipient(toEmail) };

            EmailPayload payload = new EmailPayload(from, to, subject);

            if (!content.trim().startsWith("<")) {
                content = "<html><body>" + content + "</body></html>";
            }

            payload.setHtml(content);

            Gson gson = new Gson();
            String jsonInputString = gson.toJson(payload);

            URL url = new URL(API_URL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // Read the response (if needed)
            // InputStream is = conn.getInputStream();
            // String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            // System.out.println("Response: " + response);

            conn.disconnect();
            logger.info("Email is sent to {}", toEmail);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[\r\n]", "");
    }
}
