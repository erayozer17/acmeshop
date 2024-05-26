package com.erayoezer.acmeshop.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;
    private final String defaultModel;

    public OpenAIService(@Value("${openai.api.key}") String apiKey,
                         @Value("${openai.api.url}") String apiUrl,
                         @Value("${openai.api.defaultModel}") String defaultModel) {
        this.restTemplate = new RestTemplate();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.defaultModel = defaultModel;
    }

    public String sendRequest(String prompt) {
        String responseBody = "";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(apiUrl);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + apiKey);

            JSONObject json = new JSONObject();
            json.put("model", defaultModel);
            json.put("temperature", 0.7);

            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", prompt);

            json.put("messages", new JSONArray().put(message));

            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                responseBody = EntityUtils.toString(response.getEntity());
                System.out.println(responseBody);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return getContent(responseBody);
    }

    private String getContent(String responseBody) {
        JSONObject jsonResponse = new JSONObject(responseBody);
        String content = "";
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject messageObject = firstChoice.getJSONObject("message");
            content = messageObject.getString("content");
            System.out.println(content);
        } else {
            System.out.println("No choices available in the response.");
        }
        return content;
    }
}
