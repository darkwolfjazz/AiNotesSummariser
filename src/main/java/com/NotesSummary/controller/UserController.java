package com.NotesSummary.controller;

import com.NotesSummary.dto.LoginRequestDTO;
import com.NotesSummary.dto.LoginResponseDTO;
import com.NotesSummary.entity.User;
import com.NotesSummary.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestBody User user){
      return userService.register(user);
    }

    @PostMapping("/verify")
    public String verifyOtpViaEmail(@RequestParam String email, @RequestParam String otp){
        return userService.verifyOtp(email,otp);
    }


    //old login functionality
//    @PostMapping("/login")
//    public String loginUser(@RequestBody LoginRequestDTO loginRequestDTO){
//        return userService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
//    }

//    //new login functionality
//    @PostMapping("/login")
//    public LoginResponseDTO loginUser(@RequestBody LoginRequestDTO loginRequestDTO){
//        return userService.login(loginRequestDTO.getEmail(),loginRequestDTO.getPassword());
//    }

}
