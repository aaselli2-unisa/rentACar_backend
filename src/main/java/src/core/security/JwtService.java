package src.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import src.repository.user.UserEntity;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    /** Access token TTL — keep short (≤ 1 hour in production). */
    @Value("${application.security.jwt.expiration}")
    private long expiration;
    /** Refresh token TTL — must be significantly longer than access token (7–30 days). Security patch V09. */
    @Value("${application.security.jwt.refresh-expiration}")
    private long refreshExpiration;

    // V-05: only id and role in claims — name/email/phone removed (PII in JWT payload is Base64, not encrypted)
    public String generateToken(UserEntity user) {
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("id", user.getId());
        customClaims.put("role", user.getAuthorities());
        return generateToken(customClaims, user);
    }

    public String generateToken(Map<String, Object> customClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(customClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        Map<String, Object> refreshTokenClaims = new HashMap<>();
        refreshTokenClaims.put("userId", user.getId());

        // Security patch V09: use refreshExpiration (7 days) instead of the access-token expiration.
        return Jwts.builder()
                .setClaims(refreshTokenClaims)
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            final String usernameFromToken = extractUsername(token);
            return (user.getUsername().equals(usernameFromToken)) && !isTokenExpired(token);
        } catch (ExpiredJwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody(); // Parses the data inside the JWT.
    }

    public Key getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}