package com.mahivkhanwasi.auth_service.service;

import com.mahivkhanwasi.auth_service.dto.RedisUserSession;
import com.mahivkhanwasi.auth_service.dto.RequestLoginDTO;
import com.mahivkhanwasi.auth_service.dto.RequestUserDTO;
import com.mahivkhanwasi.auth_service.model.Role;
import com.mahivkhanwasi.auth_service.model.User;
import com.mahivkhanwasi.auth_service.repository.RoleRepository;
import com.mahivkhanwasi.auth_service.repository.UserRepository;
import com.mahivkhanwasi.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.print.attribute.standard.JobKOctets;
import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RedisService redisService;


    public String register(RequestUserDTO dto){
        User user = userRepository.findByUsername(dto.username()).orElse(null);

        if(user != null){
            return "User found";
        }

        User newUser = new User();
        newUser.setUsername(dto.username());
        newUser.setPassword(passwordEncoder.encode(dto.password()));
        newUser.setFirstName(dto.firstName());
        newUser.setLastName(dto.lastName());
        newUser.setEnabled(true);

        if(dto.roles() != null){
            List<Role> roles = dto.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                    .toList();
            newUser.setRoles(roles);
        }
        userRepository.save(newUser);
        return "User registered successfully";
    }


    public Map<String, Object> login(RequestLoginDTO dto) {
        User user = userRepository.findByUsername(dto.username()).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (user == null) {
            response.put("status", "User not Found");
            return response;
        }

        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.username(),
                            dto.password().trim()
                    )
            );

            // Check if the principal is an instance of User
            if (!(auth.getPrincipal() instanceof User)) {
                response.put("status", "Authentication failed. Invalid user details.");
                return response;
            }

            var user1 = (User) auth.getPrincipal();  // Cast to User
            var claims = new HashMap<String, Object>();
            claims.put("fullName", user1.getFullName());

            String accessToken = jwtService.generateToken(claims, user1);

            RedisUserSession userSession = new RedisUserSession(
                    user1.getFullName(),
                    user1.getUsername(),
                    accessToken
            );
            redisService.set(user1.getFullName(), userSession, 7200L);

            response.put("token", accessToken);
            return response;

        } catch (Exception e) {
            e.printStackTrace();  // Print stack trace for better insight
            response.put("status", "Authentication failed");
            return response;
        }
    }

}
