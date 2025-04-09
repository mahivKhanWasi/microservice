package com.mahivkhanwasi.portfolio_service.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
public class test {

    @GetMapping()
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("Test");
    }

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/user")
    public ResponseEntity<?> test1() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 User Roles: " + authentication.getAuthorities());
        return ResponseEntity.ok("User");
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<?> test2() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("🔍 User Roles: " + authentication.getAuthorities());
        return ResponseEntity.ok("Admin");
    }
}
