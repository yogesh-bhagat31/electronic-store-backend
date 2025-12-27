package com.learn.electronicstore.config;

import com.learn.electronicstore.security.JwtAccessDeniedHandler;
import com.learn.electronicstore.security.JwtAuthFilter;
import com.learn.electronicstore.security.JwtAuthenticationEntryPoint;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;


/**
 * SecurityConfig configures Spring Security for the application.
 *
 * <p>
 * This class defines:
 * <ul>
 *   <li>URL-based authorization rules</li>
 *   <li>JWT authentication filter</li>
 *   <li>Custom authentication and authorization exception handling</li>
 *   <li>Stateless session management</li>
 *   <li>CORS and CSRF configuration</li>
 * </ul>
 *
 * <p>
 * The application uses JWT-based authentication, so no HTTP session
 * is maintained on the server.
 *
 * @author Yogesh Bhagat
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private JwtAuthFilter jwtAuthFilter;
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;



    /**
     * Configures the security filter chain.
     *
     * <p>
     * This method defines:
     * <ul>
     *   <li>Which endpoints are public</li>
     *   <li>Which endpoints require authentication</li>
     *   <li>Role-based access control</li>
     *   <li>JWT filter registration</li>
     *   <li>CORS and CSRF settings</li>
     * </ul>
     *
     * @param httpSecurity the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // configuring urls
        httpSecurity.authorizeHttpRequests(
                request -> {
                    request.requestMatchers("/users").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole("ADMIN", "NORMAL")
                            .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
                            .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                            .requestMatchers("products/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                            .requestMatchers("/categories/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                            .requestMatchers(("/carts/**")).permitAll()
                            .requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/google-login", "/auth/regenerate-token").permitAll()
                            .requestMatchers("/auth/**").authenticated()
                            .anyRequest().permitAll();

                }
        );


        //entry point :- Handle authentication exception
        httpSecurity.exceptionHandling(exceptionHandling
                -> exceptionHandling.authenticationEntryPoint(jwtAuthenticationEntryPoint));
        // access denied :- Handle authorization exception
        httpSecurity.exceptionHandling(e -> e.accessDeniedHandler(jwtAccessDeniedHandler));

        // session creation policy :- stateless or statefulll(session based)
        httpSecurity.sessionManagement(session
                -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //custom jwtAuthFilter
        httpSecurity.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOrigins(Collections.singletonList("https://obsequent-untumultuously-candi.ngrok-free.dev"));
                    corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                    corsConfiguration.setMaxAge(3600L);
                    return corsConfiguration;
                }));
        //No need of CSRF as our app is stateless.
        httpSecurity.csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }


    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}

