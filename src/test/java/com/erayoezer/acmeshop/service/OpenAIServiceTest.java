package com.erayoezer.acmeshop.service;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenAIServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private HttpEntity httpEntity;

    private OpenAIService openAIService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        openAIService = new OpenAIService("apiKey", "apiUrl", "defaultModel", restTemplate, httpClient);
    }

    @Test
    public void testSendRequest() throws Exception {
        String prompt = "test";
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", new JSONArray());
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = openAIService.sendRequest(prompt);
        assertEquals("", result);
    }

    @Test
    public void testGetContent() {
        JSONObject messageObject = new JSONObject();
        messageObject.put("content", "test content");

        JSONObject firstChoice = new JSONObject();
        firstChoice.put("message", messageObject);

        JSONArray choices = new JSONArray();
        choices.put(firstChoice);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", choices);

        String result = openAIService.getContent(jsonResponse.toString());
        assertEquals("test content", result);
    }
}
