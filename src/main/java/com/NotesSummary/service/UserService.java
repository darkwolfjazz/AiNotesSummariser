package com.NotesSummary.service;

import com.NotesSummary.component.JWTUtil;
import com.NotesSummary.dto.LoginResponseDTO;
import com.NotesSummary.entity.User;
import com.NotesSummary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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


    //Delete user
    public ResponseEntity<?>deleteUser(String email){
    Optional<User>userDeleted=userRepository.findByEmail(email);
    if(userDeleted.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with mail"+email);
    }
    User user=userDeleted.get();
    userRepository.delete(user);
    return ResponseEntity.ok(user);
    }

    //Update user details
    public ResponseEntity<?>updateUser(String email,User updatedUser){
     Optional<User>usertoBeUpdated=userRepository.findByEmail(email);
     if(usertoBeUpdated.isEmpty()){
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email"+email);
     }
     User user=usertoBeUpdated.get();
     if(updatedUser.getEmail()!=null){
         user.setEmail(updatedUser.getEmail());
     }
     if(updatedUser.getPassword()!=null && !updatedUser.getPassword().isEmpty()){
         user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
     }
     userRepository.save(user);
     return ResponseEntity.ok("User details updated successfully");
    }

    //Forgot password functionality
    public ResponseEntity<?>forgotPassword(String email){
    Optional<User>optUser=userRepository.findByEmail(email);
    if(optUser.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email"+email);
    }
    User user=optUser.get();
    String otp=String.valueOf(new Random().nextInt(899999)+100000);
    user.setOtp(otp);
    user.setOtpGeneratedtime(LocalDateTime.now());
    userRepository.save(user);
    String message="Your otp for forgot password :"+otp +"Valid only for 10 minutes";
    emailService.sendMail(email,"Forgot password OTP",message);
    return ResponseEntity.ok("Otp sent successfully if you forgot your password");
    }

    //Reset password functionality
    public ResponseEntity<?>resetPassword(String email,String otp,String newPassword){
    Optional<User>optUser=userRepository.findByEmail(email);
    if(optUser.isEmpty()){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with email"+email);
    }
    User user=optUser.get();
    if(user.getOtp()==null || !user.getOtp().equalsIgnoreCase(otp)){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
    }
    if(user.getOtpGeneratedtime().plusMinutes(10).isBefore(LocalDateTime.now())){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("OTP expired");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setOtp(null);
    user.setOtpGeneratedtime(null);
    userRepository.save(user);
    return ResponseEntity.ok("Password reset successful");
    }

}
