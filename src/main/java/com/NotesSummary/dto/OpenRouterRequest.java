package com.NotesSummary.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpenRouterRequest {

    private String model;
    private List<Map<String,String>>messages;




}
