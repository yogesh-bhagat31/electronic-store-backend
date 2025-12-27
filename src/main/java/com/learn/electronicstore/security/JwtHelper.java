package com.learn.electronicstore.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Date;

@Component
public class JwtHelper {

    @Value("${token.validity}")
    private long tokenValidity;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key jwtSecretKey;

    /**
     * Returns the cached JWT secret key used for signing and verifying tokens.
     * <p>
     * This method lazily initializes the secret key if it has not already been created.
     * The key is derived from a configured secret string by converting the string into
     * a sequence of bytes using UTF-8 encoding and then passing these bytes to
     * {@link io.jsonwebtoken.security.Keys#hmacShaKeyFor(byte[])}.
     * Here it will assign the algo based on the length of string as our secret string id of 32 bytes(256 bits)
     * then it will choose if (bitLength >= 256) {return new SecretKeySpec(bytes, "HmacSHA256");
     * </p>
     *
     * <p>
     * The generated key is an implementation of {@link javax.crypto.SecretKey}, specifically
     * designed for use with HMAC-SHA algorithms (such as HS256, HS384, HS512) in JSON Web Tokens (JWT).
     * </p>
     *
     * <p>
     * Subsequent calls to this method will return the previously created key instance, ensuring
     * that only one secret key object is generated and reused throughout the application lifecycle.
     * This prevents unnecessary re-computation and ensures consistent signing/verification of JWTs.
     * </p>
     *
     * <h3>Example Usage:</h3>
     * <pre>{@code
     * SecretKey key = getJwtSecretKey();
     * String token = Jwts.builder()
     *      .setSubject("user123")
     *      .signWith(key)
     *      .compact();
     * }</pre>
     *
     * @return the initialized {@link javax.crypto.SecretKey} instance derived from the configured secret string.
     *
     * @throws IllegalArgumentException if the secret string is null, empty, or does not meet the minimum length
     *         requirements for the HMAC-SHA algorithm.
     *
     * @see io.jsonwebtoken.security.Keys#hmacShaKeyFor(byte[])
     * @see javax.crypto.SecretKey
     */
    private Key getJwtSecretKey() {
        if (jwtSecretKey == null) {
            jwtSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        }
        return jwtSecretKey;
    }


    /**
     * Extracts the username (subject) from a given JWT token.
     * <p>
     * In a JWT, the subject usually represents the username of the authenticated
     * user. This method internally calls {@link #getClaimFromToken(String, Function)}
     * with a resolver function that retrieves the {@code subject} claim from the token.
     * </p>
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * String token = "..."; // JWT obtained after login
     * String username = jwtUtil.getUsernameFromJwtToken(token);
     * System.out.println("Username: " + username);
     * }</pre>
     *
     * @param token the JWT token from which the username is to be extracted.
     *              Must be a valid, signed token.
     * @return the username (subject) contained in the token.
     * @throws io.jsonwebtoken.JwtException if the token is invalid, malformed,
     *         or the signature validation fails.
     * @see #getClaimFromToken(String, Function)
     */
    public String getUsernameFromJwtToken(String token) {
        return getClaimFromToken(token, claims -> claims.getSubject());
    }

    /**
     * Extracts a specific claim (piece of information) from a JWT token.
     *
     * <p>This method first retrieves all the claims stored inside the token
     * by calling {@link #getAllClaimsFromToken(String)}. Then it applies the
     * given resolver function on those claims to pick out the specific
     * information you want.</p>
     *
     * <p>For example, if you pass a function like:
     * <pre>{@code claims -> claims.getSubject()}</pre>
     * it will return the username (the subject) from the token.</p>
     *
     * @param <R> the type of value you want to extract from the claims
     *            (for example, {@code String} if you want the subject,
     *            or {@code Date} if you want expiration time).
     * @param token the JWT token string from which claims should be extracted.
     *              Must not be null or empty.
     * @param claimsResolver a function that takes the {@link Claims} object
     *                       and extracts a particular value from it.
     *                       Must not be null.
     * @return the value extracted from the claims, based on the
     *         {@code claimsResolver} function.
     * @throws io.jsonwebtoken.JwtException if the token is invalid,
     *                                      expired, or cannot be parsed.
     *
     * @see #getAllClaimsFromToken(String)
     * @see io.jsonwebtoken.Claims
     */
    public <R> R getClaimFromToken(String token, Function<Claims, R> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts a specific value related to the token's expiration by applying the provided
     * {@link Function} to the {@link Claims} object.
     * <p>
     * This method first retrieves all claims from the given JWT token. Then it uses the
     * provided function (claimsResolver) to pull out the required information from the claims,
     * usually the expiration date.
     * </p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Date expiration = getExpirationFromToken(token, Claims::getExpiration);
     * }</pre>
     *
     * @param <R>            the type of the value you want to extract (for example, {@link java.util.Date})
     * @param token          the JWT token string from which the claims will be read
     * @param claimsResolver a function that takes {@link Claims} and returns the desired result
     *                       (for example, claims.getExpiration())
     * @return the resolved claim value from the token (for example, the expiration date)
     * @throws io.jsonwebtoken.JwtException if the token is invalid, expired, or cannot be parsed
     */
    public <R> R getExpirationFromToken(String token, Function<Claims, R> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    /**
     * Retrieves all the claims (payload data) from the given JWT token.
     * <p>
     * This method uses the JJWT parser to:
     * <ul>
     *   <li>Set the signing key for signature verification using {@code getJwtSecretKey()}.</li>
     *   <li>Build a parser with verification enabled.</li>
     *   <li>Parse the JWT token into a {@link Claims} object (the body of the token).</li>
     * </ul>
     * <p>
     * If the token is invalid, expired, or the signature does not match,
     * this method will throw an appropriate exception
     * (such as {@link io.jsonwebtoken.security.SecurityException} or
     * {@link io.jsonwebtoken.ExpiredJwtException}).
     * </p>
     *
     * @param token the JWT token string to parse; must not be {@code null}.
     * @return the {@link Claims} contained in the JWT payload.
     *
     * @throws io.jsonwebtoken.security.SecurityException if the token's signature is invalid.
     * @throws io.jsonwebtoken.ExpiredJwtException if the token is expired.
     * @throws io.jsonwebtoken.MalformedJwtException if the token string is not a valid JWT.
     * @throws IllegalArgumentException if the token is {@code null} or empty.
     *
     * @implNote
     * This method relies on the JJWT 0.12.x style API where {@code parser()} returns
     * a parser builder. The signing key is configured via {@code verifyWith()}
     * and the final claims are extracted from {@code parseSignedClaims(token)}.
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getJwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    /**
     * Determines whether the provided JSON Web Token (JWT) has expired.
     * <p>
     * This method extracts the {@link Date expiration date} from the token's claims
     * using {@link #getExpirationFromToken(String, java.util.function.Function)} and
     * compares it with the current system time. If the expiration date is earlier
     * than the current time, the token is considered expired.
     * </p>
     *
     * <h3>Usage Example:</h3>
     * <pre>{@code
     * boolean expired = tokenProvider.isTokenExpired(jwtToken);
     * if (expired) {
     *     throw new TokenExpiredException("JWT has expired");
     * }
     * }</pre>
     *
     * @param token the JWT string to validate; must be a non-null, signed token.
     * @return {@code true} if the token is expired; {@code false} otherwise.
     *
     * @throws io.jsonwebtoken.JwtException if the token is invalid, malformed,
     *                                      or cannot be parsed.
     * @throws IllegalArgumentException     if the token string is {@code null} or empty.
     *
     * @see #getExpirationFromToken(String, java.util.function.Function)
     * @see io.jsonwebtoken.Claims#getExpiration()
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }


    /**
     * Generates a new JWT token for the given user.
     * <p>
     * This method delegates the actual token creation to
     * {@link #doGenerateToken(Map, String)} with an empty claims map.
     *
     * @param username the user details object containing the username
     * @return the generated JWT token as a compact {@code String}
     */
    public String generateToken(UserDetails username) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, username.getUsername());
    }


    /**
     * Generates a signed JSON Web Token (JWT) for the given user or subject.
     * <p>
     * A JWT is made of three parts:
     * <b>Header</b>, <b>Payload</b>, and <b>Signature</b>.
     * This method builds all three parts and then combines them into a final token.
     * </p>
     *
     * <h2>How it works step by step:</h2>
     * <ol>
     *   <li><b>Header</b>:
     *       - Sets the token type as "JWT".
     *       - The signing algorithm is taken automatically from {@code signWith()} method.</li>
     *
     *   <li><b>Payload (Claims)</b>:
     *       - Custom claims are added using {@code claims(claims)}.
     *       - A "subject" (usually username or userId) is set using {@code subject(subject)}.
     *       - Token creation time is recorded using {@code issuedAt(new Date(...))}.
     *       - Token expiry time is set using {@code expiration(new Date(...))}.</li>
     *
     *   <li><b>Signature</b>:
     *       - The token is signed with a secret key provided by {@code getJwtSecretKey()}.
     *       - This ensures the token cannot be tampered with, since verification will fail if changed.</li>
     *
     *   <li><b>Compact form</b>:
     *       - Finally, the three parts (header, payload, signature) are combined and
     *         converted into a single string using {@code compact()}.
     *       - The result looks like: {@code xxxxx.yyyyy.zzzzz} (Header.Payload.Signature).</li>
     * </ol>
     *
     * <h2>Example usage:</h2>
     * <pre>{@code
     * Map<String, Object> claims = new HashMap<>();
     * claims.put("role", "ADMIN");
     *
     * String token = generateToken(claims, "john_doe");
     * System.out.println(token);
     * }</pre>
     *
     * <p>
     * The above call creates a JWT for the subject "john_doe" with role "ADMIN".
     * </p>
     *
     * @param claims   key-value pairs (extra data) to be stored in the token payload
     * @param subject  the main identifier of the token (example: username or user ID)
     * @return a complete signed JWT string in the format <b>header.payload.signature</b>
     */
    public String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .header().type("JWT").and()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(getJwtSecretKey())
                .compact();
    }


    /**
     * Validates a JWT token against the provided username.
     * <p>
     * This method extracts the username embedded within the JWT token payload
     * and compares it with the expected {@code username}. Additionally,
     * it verifies whether the token has expired. A token is considered valid
     * only if:
     * <ul>
     *   <li>The extracted username matches the provided {@code username}.</li>
     *   <li>The token has not expired (i.e., the expiration time is after the current time).</li>
     * </ul>
     * </p>
     *
     * <p>
     * This method is typically used during authentication or authorization
     * to ensure the provided token is still valid for the given user.
     * </p>
     *
     * @param token the JWT token to be validated; must not be {@code null}
     * @param username the expected username to match against the token's subject; must not be {@code null}
     * @return {@code true} if the token is valid (username matches and token is not expired);
     *         {@code false} otherwise
     * @throws io.jsonwebtoken.JwtException if the token string is invalid, malformed,
     *         or the signature cannot be verified
     * @throws IllegalArgumentException if {@code token} or {@code username} is {@code null}
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromJwtToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
