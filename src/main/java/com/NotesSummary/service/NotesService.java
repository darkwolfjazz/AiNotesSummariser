package com.NotesSummary.service;

import com.NotesSummary.entity.Notes;
import com.NotesSummary.entity.User;
import com.NotesSummary.repository.NoteRepository;
import com.NotesSummary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class NotesService {

    @Autowired
    private AiService aiService;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    public String createSummary(String inputText){
        String email=getCurrentUserEmail();
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        String summary= aiService.generateSummary(inputText).block();//<- block() method to get String
            System.out.println("Generating summary from service");
            Notes notes=new Notes();
            notes.setOriginalText(inputText);
            notes.setSummary(summary);
            notes.setUser(user);
            notes.onCreate();
            noteRepository.save(notes);
            return summary;
    }


    public String getCurrentUserEmail(){
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Auth User" +SecurityContextHolder.getContext().getAuthentication());
        if(principal instanceof UserDetails){
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }









}
