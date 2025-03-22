package com.mahivkhanwasi.auth_service;

import com.mahivkhanwasi.auth_service.model.Role;
import com.mahivkhanwasi.auth_service.model.User;
import com.mahivkhanwasi.auth_service.repository.RoleRepository;
import com.mahivkhanwasi.auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}


	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
		return args -> {
			if(roleRepository.findByName("USER").isEmpty()){

				Role role = Role.builder().name("USER").build();
				roleRepository.save(
						role
				);
			}

			var userRole = roleRepository.findByName("USER")
					.orElseThrow(() -> new IllegalStateException("ROLE User not initialized"));

			User check = userRepository.findByUsername("admin@gmail.com").orElse(null);
			if(check == null){

				var user = new User();
				user.setUsername("admin@gmail.com");
				user.setPassword(passwordEncoder.encode("11223344"));
				user.setFirstName("Admin");
				user.setLastName("Admin");
				user.setAccountLocked(true);
				user.setEnabled(true);
				user.setRoles(List.of(userRole));

				userRepository.save(user);
			}

		};
	}
}
