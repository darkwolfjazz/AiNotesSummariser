package com.NotesSummary.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter public class NoteRequestDTO {
@NotBlank
private String originalText;
}
