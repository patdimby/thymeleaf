package bootstrap.web.thymeleaf.security;
import bootstrap.web.thymeleaf.model.User;
import bootstrap.web.thymeleaf.model.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import static org.assertj.core.api.Assertions.*;
class JwtServiceTests {
    private static final String RAW = "a-test-key-long-enough-for-hs256-signing";
    private static final String SECRET = Encoders.BASE64.encode(RAW.getBytes(StandardCharsets.UTF_8));
    private final JwtService service = new JwtService(SECRET, 86400000);
    private User account() { User user = new User(); user.setEmail("ada@example.com"); user.setRole(UserRole.ROLE_USER); return user; }
    private UserDetails details(String email) { return org.springframework.security.core.userdetails.User.withUsername(email).password("hash").authorities("ROLE_USER").build(); }
    @Test void tokenContainsIdentityRoleAndExpiration() {
        String token = service.generateToken(account());
        assertThat(service.extractUsername(token)).isEqualTo("ada@example.com");
        assertThat(service.<String>extractClaim(token, claims -> claims.get("role", String.class))).isEqualTo("ROLE_USER");
        assertThat(service.<Date>extractClaim(token, Claims::getExpiration)).isAfter(new Date());
        assertThat(service.isTokenValid(token, details("ada@example.com"))).isTrue();
    }
    @Test void wrongSubjectIsNotValid() { assertThat(service.isTokenValid(service.generateToken(account()), details("other@example.com"))).isFalse(); }
    @Test void stableConfiguredKeyWorksAcrossServiceInstances() {
        assertThat(new JwtService(SECRET, 86400000).extractUsername(service.generateToken(account()))).isEqualTo("ada@example.com");
    }
    @Test void wrongSignatureIsRejected() {
        assertThatThrownBy(() -> new JwtService("", 86400000).extractUsername(service.generateToken(account()))).isInstanceOf(JwtException.class);
    }
    @Test void malformedTokenIsRejected() { assertThatThrownBy(() -> service.extractUsername("garbage")).isInstanceOf(JwtException.class); }
    @Test void expiredTokenIsRejected() {
        String token = Jwts.builder().subject("ada@example.com").expiration(new Date(1)).signWith(Keys.hmacShaKeyFor(RAW.getBytes(StandardCharsets.UTF_8))).compact();
        assertThatThrownBy(() -> service.extractUsername(token)).isInstanceOf(ExpiredJwtException.class);
    }
    @Test void weakKeyIsRejected() { assertThatThrownBy(() -> new JwtService(Encoders.BASE64.encode(new byte[4]), 1000)).isInstanceOf(io.jsonwebtoken.security.WeakKeyException.class); }
    @Test void invalidLifetimeIsRejected() { assertThatThrownBy(() -> new JwtService(SECRET, 0)).isInstanceOf(IllegalArgumentException.class); }
}
