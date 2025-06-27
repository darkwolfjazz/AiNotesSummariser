package com.NotesSummary.controller;

import com.NotesSummary.entity.User;
import com.NotesSummary.service.AiService;
import com.NotesSummary.service.NotesService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/summary")
public class AiServiceController {



    @Autowired
    public AiService aiService;

    @Autowired
    private NotesService notesService;

    @PostMapping
    public ResponseEntity<String>getSummary(@RequestBody String inputText){
        System.out.println("Inside controller for summary");
        try{
            String summary=notesService.createSummary(inputText);
            return ResponseEntity.ok(summary);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error"+e.getMessage());
        }
    }
}
