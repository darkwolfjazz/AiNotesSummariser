package com.NotesSummary.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponseDTO {

private String token;
private String message;

}
