package com.learn.electronicstore;

import com.learn.electronicstore.entities.User;
import com.learn.electronicstore.repositories.UserRepository;
import com.learn.electronicstore.security.JwtHelper;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElectronicstoreApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtHelper jwtHelper;


    @Test
    void contextLoads() {
    }

    @Test
    void testToken(){
        User user = userRepository.findByEmail("yogesh@gmail.com").get();
        String token = jwtHelper.generateToken(user);
        System.out.println(token);

        System.out.println("Get username from token");
        System.out.println(jwtHelper.getUsernameFromJwtToken(token));

        System.out.println("Token is expired?");
        System.out.println(jwtHelper.isTokenExpired(token));

        System.out.println("Get expiry date from token");
        System.out.println(jwtHelper.getExpirationFromToken(token, Claims::getExpiration));;
    }

}
