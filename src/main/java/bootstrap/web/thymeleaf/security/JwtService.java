package bootstrap.web.thymeleaf.security;

import bootstrap.web.thymeleaf.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey secretKey;
    private final long expirationMillis;

    public JwtService(@Value("${app.jwt.secret:}") String encodedSecret,
                      @Value("${app.jwt.expiration-millis:86400000}") long expirationMillis) {
        // A configured key survives restarts; an ephemeral local key does not.
        this.secretKey = encodedSecret.isBlank() ? Jwts.SIG.HS256.key().build()
                : Keys.hmacShaKeyFor(Decoders.BASE64.decode(encodedSecret));
        if (expirationMillis <= 0) throw new IllegalArgumentException("JWT lifetime must be positive");
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(User user) {
        Date issued = new Date();
        return Jwts.builder().subject(user.getEmail()).claim("role", user.getRole().name())
                .issuedAt(issued).expiration(new Date(issued.getTime() + expirationMillis))
                .signWith(secretKey, Jwts.SIG.HS256).compact();
    }

    public boolean isTokenValid(String token, UserDetails details) {
        Claims claims = extractAllClaims(token);
        return Objects.equals(claims.getSubject(), details.getUsername())
                && claims.getExpiration() != null && claims.getExpiration().after(new Date());
    }

    public String extractUsername(String token) { return extractClaim(token, Claims::getSubject); }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        // Verify the signature before any identity claims are trusted.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
    }
}
