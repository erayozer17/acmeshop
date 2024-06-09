package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.AiModel;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OpenAIService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIService.class);

    private final CloseableHttpClient httpClient;
    private final String apiKey;
    private final String apiUrl;

    @Autowired
    public OpenAIService(@Value("${openai.api.key}") String apiKey,
                         @Value("${openai.api.url}") String apiUrl,
                         CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String sendRequest(String prompt, AiModel aiModel) {
        String responseBody = "";
        try {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);

            JSONObject json = new JSONObject();
            AiModel aiModelGet = aiModel;
            if (aiModelGet == null) {
                aiModelGet = AiModel.GPT3;
            }
            json.put("model", aiModelGet.getValue());
            json.put("temperature", 0.7);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            json.put("messages", new JSONArray().put(message));

            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                responseBody = EntityUtils.toString(response.getEntity());
                logger.info(String.format("Response body is: %s", responseBody));
            } catch (ParseException e) {
                logger.error(String.format("Response could not be parsed. Response body is: %s", responseBody));
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            logger.error(String.format("IOException while open ai call. %s", e.getMessage()));
        }
        return getContent(responseBody);
    }

    protected String getContent(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        String content = "";
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObject = firstChoice.getJSONObject("message");
            content = messageObject.getString("content");
        } else {
            logger.error(String.format("No choices available in the response. Response body: %s", responseBody));
        }
        return content;
    }
}
