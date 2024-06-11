package com.erayoezer.acmeshop.service.ai;

import com.erayoezer.acmeshop.model.AiModel;

public interface AiService {
    String sendRequest(String prompt, AiModel aiModel);
}
