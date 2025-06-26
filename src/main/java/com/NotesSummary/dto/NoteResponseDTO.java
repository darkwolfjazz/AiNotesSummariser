package com.NotesSummary.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NoteResponseDTO {

private Long id;
private String summary;
private LocalDateTime createdAt;

}
