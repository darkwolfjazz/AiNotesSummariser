package com.NotesSummary.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Notes {


   @Id
   private String id;
   private String originalText;
   private String summary;
   private LocalDateTime createdAt;
   @DBRef
   private User user;

    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }


}
