package com.NotesSummary.service;

import com.NotesSummary.dto.OpenRouterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    @Autowired
    private final WebClient openRouterWebClient;
    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.model.backup}")
    private String backUpModel;


    public Mono<String>generateSummary(String inputText){
        Map<String,String>userMessage=new HashMap<>();
        userMessage.put("role","user");
        userMessage.put("content","Summarize : \n"+inputText);

        if(inputText.length()<50){
            return Mono.just("Please provide actual chapter text to summarize. The input is too short.\"");
        }
        OpenRouterRequest request=new OpenRouterRequest(model, Collections.singletonList(userMessage));
        return openRouterWebClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }).onErrorResume(error->{
                    System.out.println("Primary model failed "+error.getMessage());
                    return callBackUpModel(inputText,backUpModel);
                });
    }

    public Mono<String>callBackUpModel(String inputText,String modelToUse){
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Summarize : \n" + inputText);
        if(inputText.length()<50){
            return Mono.just("Please provide actual chapter text to summarize. The input is too short.\"");
        }
        OpenRouterRequest request=new OpenRouterRequest(modelToUse,Collections.singletonList(userMessage));
        return openRouterWebClient
                .post()
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                });
    }
}
