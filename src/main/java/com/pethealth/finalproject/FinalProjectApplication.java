package com.pethealth.finalproject;

import com.pethealth.finalproject.model.Owner;
import com.pethealth.finalproject.model.Veterinarian;
import com.pethealth.finalproject.security.models.Role;
import com.pethealth.finalproject.security.models.User;
import com.pethealth.finalproject.security.services.impl.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class FinalProjectApplication {

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

//			userService.saveUser(new User(null, "Katia", "katia", "1234", new ArrayList<>()));
			userService.saveUser(new Owner(null, "Katia", "katia", "1234", new ArrayList<>(), "katia@mail.com"));
//			userService.saveUser(new User(null, "Laia Fernández", "laia", "1234", new ArrayList<>()));
			userService.saveUser(new Veterinarian(null, "Laia Fernández", "laia", "1234", new ArrayList<>(), "laia@pethealth.com"));
			userService.saveUser(new User(null, "Super Admin", "admin", "1234", new ArrayList<>()));

			userService.addRoleToUser("katia", "ROLE_USER");
			userService.addRoleToUser("katia", "ROLE_ADMIN");
			userService.addRoleToUser("laia", "ROLE_VET");
			userService.addRoleToUser("laia", "ROLE_USER");
		};
	}

}
