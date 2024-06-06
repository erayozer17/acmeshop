package com.erayoezer.acmeshop.model;

public enum AiModel {
    GPT3("gpt-3.5-turbo"),
    GPT4("gpt-4o");

    private final String value;

    AiModel(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
