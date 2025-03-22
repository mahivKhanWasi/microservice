package com.mahivkhanwasi.auth_service.controller;


import com.mahivkhanwasi.auth_service.dto.RequestLoginDTO;
import com.mahivkhanwasi.auth_service.dto.RequestUserDTO;
import com.mahivkhanwasi.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
public class UserController {

    private final UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RequestUserDTO dto){
        return ResponseEntity.ok(userService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RequestLoginDTO dto){
        return ResponseEntity.ok(userService.login(dto));
    }
}
