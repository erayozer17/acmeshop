package com.erayoezer.acmeshop.controller;

import com.erayoezer.acmeshop.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/openai")
public class OpenAIController {

    @Autowired
    private OpenAIService openAIService;

    @GetMapping
    public String getOpenAIResponse(@RequestParam String prompt) {
        return openAIService.sendRequest(prompt);
    }
}
