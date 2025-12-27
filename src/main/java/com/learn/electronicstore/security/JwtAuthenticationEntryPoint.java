package com.learn.electronicstore.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is called when a user tries to access a protected resource
     * without proper authentication. It starts the authentication failure
     * handling process.
     *
     * <p>It sets the HTTP response status to {@code 401 Unauthorized}
     * and writes a simple error message to the response body.
     * The error message includes the text "Unauthorized" followed
     * by the specific reason provided by the {@link AuthenticationException}.</p>
     *
     * @param request  the {@link HttpServletRequest} that contains the
     *                 client request details
     * @param response the {@link HttpServletResponse} used to return the
     *                 HTTP status code and error message to the client
     * @param authException the {@link AuthenticationException} that explains
     *                      why the authentication failed
     * @throws IOException if an input or output error occurs while writing
     *                     the error message to the response
     * @throws ServletException if a servlet-related error occurs during
     *                          the authentication failure handling
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println("Omg! " + authException.getMessage());
    }
}



