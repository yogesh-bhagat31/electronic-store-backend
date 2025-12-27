package com.learn.electronicstore;

import com.learn.electronicstore.entities.Role;
import com.learn.electronicstore.entities.User;
import com.learn.electronicstore.repositories.RoleRepository;
import com.learn.electronicstore.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class ElectronicstoreApplication implements CommandLineRunner {

    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public ElectronicstoreApplication(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(ElectronicstoreApplication.class, args);
        System.out.println("TOKEN_SECRET: " + System.getenv("TOKEN_SECRET"));
        System.out.println(System.getProperty("java.io.tmpdir"));

    }


    @Override
    public void run(String... args) throws Exception {

        Role roleAdmin1 = roleRepository.findByName("ROLE_ADMIN").orElse(null);

        if (roleAdmin1 == null) {
            Role role1 = new Role();
            role1.setRoleId(UUID.randomUUID().toString());
            role1.setName("ROLE_ADMIN");
            roleRepository.save(role1);
        }

        Role roleAdmin2 = roleRepository.findByName("ROLE_NORMAL").orElse(null);
        if (roleAdmin2 == null) {
            Role role2 = new Role();
            role2.setRoleId(UUID.randomUUID().toString());
            role2.setName("ROLE_NORMAL");
            roleRepository.save(role2);
        }

        User user = userRepository.findByEmail("yogesh@gmail.com").orElse(null);
        if (user == null) {
            user = new User();
            user.setName("Yogesh");
            user.setEmail("yogesh@gmail.com");
            user.setPassword(passwordEncoder.encode("yogesh"));
            user.setRoles(List.of(roleAdmin1));
            user.setUserId(UUID.randomUUID().toString());
            userRepository.save(user);
        }

    }
}
