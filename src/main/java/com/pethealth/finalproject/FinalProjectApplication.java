package com.pethealth.finalproject;

import com.pethealth.finalproject.model.*;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pethealth.finalproject.repository.PetRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication
public class FinalProjectApplication {

	@Autowired
	private PetRepository petRepository; //temporal!

	public static void main(String[] args) {
		SpringApplication.run(FinalProjectApplication.class, args);
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_VET"));
//
			Owner katia = new Owner("Katia", "katia", "1234", new ArrayList<>(), "katia@mail.com");
			userService.saveUser(katia);
			userService.saveUser(new Veterinarian("Laia Fernández", "laia", "1234", new ArrayList<>(), "laia@pethealth.com"));
			userService.saveUser(new Admin("Super Admin", "admin", "1234", new ArrayList<>()));
//
			userService.addRoleToUser("katia", "ROLE_USER");
			userService.addRoleToUser("katia", "ROLE_ADMIN");
//			userService.addRoleToUser("laia", "ROLE_VET");
//			userService.addRoleToUser("laia", "ROLE_USER");
//			userService.addRoleToUser("admin", "ROLE_ADMIN");
		};
	}

}
