package com.erayoezer.acmeshop.service;

import com.erayoezer.acmeshop.model.AiModel;
import com.erayoezer.acmeshop.service.ai.OpenAIServiceImpl;
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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OpenAIServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private HttpEntity httpEntity;

    private OpenAIServiceImpl aiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        aiService = new OpenAIServiceImpl("apiKey", "apiUrl", httpClient);
    }

    @Test
    public void testSendRequest() throws Exception {
        String prompt = "test";
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", new JSONArray());
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = aiService.sendRequest(prompt, null);
        assertEquals("", result);
    }

    @Test
    public void testGetContent() throws IOException {
        JSONObject messageObject = new JSONObject();
        messageObject.put("content", "test content");

        JSONObject firstChoice = new JSONObject();
        firstChoice.put("message", messageObject);

        JSONArray choices = new JSONArray();
        choices.put(firstChoice);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", choices);
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = aiService.sendRequest(jsonResponse.toString(), AiModel.GPT3);
        assertEquals("test content", result);
    }

    @Test
    public void testSendRequestParseException() throws Exception {
        String prompt = "test";
        StringEntity responseEntity = new StringEntity("invalid json");
        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        assertThrows(RuntimeException.class, () -> aiService.sendRequest(prompt, null));
    }

    @Test
    public void testSendRequestWithAiModel() throws Exception {
        String prompt = "test";
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", new JSONArray());
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = aiService.sendRequest(prompt, AiModel.GPT4);
        assertEquals("", result);
    }

    @Test
    public void testSendRequestWithNullAiModel() throws Exception {
        String prompt = "test";
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", new JSONArray());
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = aiService.sendRequest(prompt, null);
        assertEquals("", result);
    }

    @Test
    public void testGetContentWithNoChoices() throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("choices", new JSONArray());
        StringEntity responseEntity = new StringEntity(jsonResponse.toString());

        when(httpResponse.getEntity()).thenReturn(responseEntity);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        String result = aiService.sendRequest(jsonResponse.toString(), AiModel.GPT3);
        assertEquals("", result);
    }
}
