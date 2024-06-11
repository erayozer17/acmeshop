package com.erayoezer.acmeshop.service.email;

import com.erayoezer.acmeshop.model.email.EmailPayload;
import com.google.gson.Gson;
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
public class MailerSendImpl implements EmailSender {

    private static final Logger logger = LoggerFactory.getLogger(MailerSendImpl.class);

    private final String apiUrl;
    private final String token;

    @Autowired
    public MailerSendImpl(@Value("${email.token}") String token,
                          @Value("${email.apiUrl}") String apiUrl) {
        this.token = token;
        this.apiUrl = apiUrl;
    }

    @Override
    public void sendEmail(EmailPayload payload) {
        try {
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(payload);

            URL url = new URL(apiUrl);

            HttpURLConnection conn = getHttpURLConnection(url, jsonInputString);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            conn.disconnect();
            logger.info("Email is sent to {} responseCode: {}", payload.getTo()[0], conn.getResponseCode());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpURLConnection getHttpURLConnection(URL url, String jsonInputString) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setDoOutput(true);
        return conn;
    }

}
