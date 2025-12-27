package com.learn.electronicstore.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtHelper jwtHelper;
    private UserDetailsService userDetailsService;

    /**
     * Processes an incoming HTTP request to perform JWT-based authentication
     * before delegating it to the next filter in the chain.
     *
     * <p>This filter:
     * <ul>
     *   <li>Reads the <b>Authorization</b> HTTP header.</li>
     *   <li>Extracts the JWT token if the header value starts with "Bearer ".</li>
     *   <li>Parses the token to obtain the username.</li>
     *   <li>Validates the token against stored user details.</li>
     *   <li>If valid, sets the Authentication object in the SecurityContext.</li>
     * </ul>
     *
     * <p>If no valid token is found, the filter simply passes the request along
     * without setting authentication.
     *
     * <h3>Processing Steps:</h3>
     * <ol>
     *   <li>Retrieve "Authorization" header from the request.</li>
     *   <li>Check if it starts with "Bearer ".</li>
     *   <li>If yes, remove the "Bearer " prefix to obtain the JWT string.</li>
     *   <li>Use {@code jwtHelper.getUsernameFromJwtToken()} to extract username.</li>
     *   <li>Handle parsing exceptions:
     *       <ul>
     *         <li>{@link IllegalArgumentException} - token is invalid.</li>
     *         <li>{@link ExpiredJwtException} - token has expired.</li>
     *         <li>{@link MalformedJwtException} - token is malformed (possibly tampered).</li>
     *       </ul>
     *   </li>
     *   <li>If username is not null and no authentication is set in the SecurityContext:
     *       <ul>
     *         <li>Load {@link UserDetails} from the database.</li>
     *         <li>Validate the token using {@code jwtHelper.validateToken()}.</li>
     *         <li>If valid, create a {@link UsernamePasswordAuthenticationToken}
     *             with the user's authorities and set it in the SecurityContext.</li>
     *       </ul>
     *   </li>
     *   <li>Call {@code filterChain.doFilter()} to pass control to the next filter.</li>
     * </ol>
     *
     * @param request the {@link HttpServletRequest} containing client request data
     * @param response the {@link HttpServletResponse} for sending data to the client
     * @param filterChain the {@link FilterChain} for invoking the next filter
     * @throws ServletException if an error occurs in the filtering process
     * @throws IOException if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        log.info("request header: " + requestHeader);

        String username = null;
        String jwtToken = null;

        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {

            jwtToken = requestHeader.substring(7);
            try {
                username = jwtHelper.getUsernameFromJwtToken(jwtToken);
                log.info("token username: " + username);

            } catch (IllegalArgumentException e) {
                log.error("invalid token: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                log.error("expired token: " + e.getMessage());
            } catch (MalformedJwtException e) {
                log.error("malformed token someone has tempered it: " + e.getMessage());
            } catch (Exception e) {
                log.error("error: " + e.getMessage());
            }
        } else {
            log.info("invalid request header it does not start with Bearer : " + requestHeader);
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtHelper.validateToken(jwtToken, userDetails.getUsername())) {

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}

