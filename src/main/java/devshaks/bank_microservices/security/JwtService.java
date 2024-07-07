package devshaks.bank_microservices.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for managing JSON Web Tokens (JWT).
 */
public class JwtService {

    // JWT expiration time, injected from application properties
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    // Secret key for signing JWTs, injected from application properties
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    /**
     * Extracts the username from the JWT token.
     *
     * @param token The JWT token
     * @return The username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from the JWT token.
     *
     * @param token The JWT token
     * @param claimTResolver Function to resolve the claim from the token
     * @param <T> The type of the claim
     * @return The extracted claim
     */
    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimTResolver) {
        final Claims claims = extractAllClaims(token);
        return claimTResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token The JWT token
     * @return The extracted claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param userDetails The user details
     * @return The generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Generates a JWT token with extra claims for the given user details.
     *
     * @param claims The extra claims
     * @param userDetails The user details
     * @return The generated JWT token
     */
    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        return buildToken(claims, userDetails, jwtExpiration);
    }

    /**
     * Builds a JWT token with the given claims, user details, and expiration time.
     *
     * @param extraClaims The extra claims
     * @param userDetails The user details
     * @param jwtExpiration The expiration time in milliseconds
     * @return The built JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, @NotNull UserDetails userDetails, long jwtExpiration) {
        var authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities", authorities)
                .signWith(getSignInKey())
                .compact();
    }

    /**
     * Validates the JWT token against the given user details.
     *
     * @param token The JWT token
     * @param userDetails The user details
     * @return True if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token, @NotNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token The JWT token
     * @return True if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token The JWT token
     * @return The expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Gets the signing key for JWT token operations.
     *
     * @return The signing key
     */
    private @NotNull Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        if (keyBytes.length * 8 < 256) {
            throw new IllegalArgumentException("Secret key must be at least 256 bits long");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
