package bootstrap.web.thymeleaf.service;
import bootstrap.web.thymeleaf.model.User;
import bootstrap.web.thymeleaf.model.UserRole;
import bootstrap.web.thymeleaf.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class UserDetailsTests {
    private final UserRepository repository = mock(UserRepository.class);
    private final CustomUserDetailsService service = new CustomUserDetailsService(repository);
    @Test void loadsEmailPasswordAndAuthority() {
        User user = new User(); user.setEmail("ada@example.com"); user.setPassword("bcrypt-hash"); user.setRole(UserRole.ROLE_ADMIN);
        when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        var details = service.loadUserByUsername(user.getEmail());
        assertThat(details.getUsername()).isEqualTo(user.getEmail());
        assertThat(details.getPassword()).isEqualTo("bcrypt-hash");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }
    @Test void missingUserFailsAuthentication() {
        when(repository.findByEmail("missing@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.loadUserByUsername("missing@example.com")).isInstanceOf(UsernameNotFoundException.class);
    }
}
