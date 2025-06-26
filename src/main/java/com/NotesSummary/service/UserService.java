package com.NotesSummary.service;

import com.NotesSummary.component.JWTUtil;
import com.NotesSummary.dto.LoginResponseDTO;
import com.NotesSummary.entity.User;
import com.NotesSummary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

@Autowired
private UserRepository userRepository;

private final BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();


private final Map<String,User>tempUserMap=new HashMap<>();


@Autowired
private EmailService emailService;

@Autowired
private JWTUtil jwtUtil;

public String register(User user){
    if(userRepository.findByEmail(user.getEmail()).isPresent()){
        return "User email already exists";
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    //Generate OTP
    String otp=String.valueOf(new Random().nextInt(899999)+100000);
    user.setOtp(otp);
    user.setOtpGeneratedtime(LocalDateTime.now());
    user.setVerified(false);
    //userRepository.save(user);
    tempUserMap.put(user.getEmail(), user);

    //Send otp for verification
    String message="Your OTP for verification is: " + otp + "\nValid for 10 minutes.";
    emailService.sendMail(user.getEmail(),"OTP for verification",message);
    return "Otp sent successfully please verify";



}
public String verifyOtp(String email,String otp){
    User user=tempUserMap.get(email);
    if(user==null){
        return "Invalid email";
    }
    if(user.isVerified()){
        return "Already verified";
    }
    if(user.getOtpGeneratedtime().plusMinutes(10).isBefore(LocalDateTime.now())){
        tempUserMap.remove(email);
        return "Otp expired";
    }

    if(!user.getOtp().equalsIgnoreCase(otp)){
        return "Incorrect OTP";
    }
    user.setVerified(true);
    user.setOtp(null);
    user.setOtpGeneratedtime(null);
    userRepository.save(user);
    tempUserMap.remove(email);
    emailService.sendMail(user.getEmail(), "Congrats OTP verified!","Your email is now verified");
   return "Email verified and account created successfully";
}


//Old Login functionality starts here
//    public String login(String email,String password){
//    Optional<User>optUser=userRepository.findByEmail(email);
//    if(optUser.isEmpty()){
//        return "Invalid email or password";
//    }
//    User user=optUser.get();
//    if(!user.isVerified()){
//        return "Email not verified";
//    }
//    boolean passwordMatch= passwordEncoder.matches(password, user.getPassword());
//    if(!passwordMatch){
//        return "Invalid password";
//    }
//    return "Login successfull";
//    }

  //Login functionality with JWT starts
    public LoginResponseDTO login(String email,String password){
    Optional<User>optUser=userRepository.findByEmail(email);
    if(optUser.isEmpty()){
        return new LoginResponseDTO(null,"Invalid email or password");
    }

    User user=optUser.get();
    if(!user.isVerified()){
        return new LoginResponseDTO(null,"User email is not verified");
    }
    boolean passwordMatch= passwordEncoder.matches(password, user.getPassword());
    if(!passwordMatch){
        return new LoginResponseDTO(null,"Invalid password");
    }
    String token=jwtUtil.generateToken(email);
    return new LoginResponseDTO(token,"Login successfull");
    }



}
