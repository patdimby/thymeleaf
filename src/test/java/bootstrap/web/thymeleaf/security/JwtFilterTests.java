package bootstrap.web.thymeleaf.security;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class JwtFilterTests {
    private final JwtService jwt = mock(JwtService.class);
    private final UserDetailsService users = mock(UserDetailsService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwt, users);
    @AfterEach void clearContext() { SecurityContextHolder.clearContext(); }
    @Test void noBearerHeaderContinuesWithoutAuthentication() throws Exception {
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(new MockHttpServletRequest(), new MockHttpServletResponse(), chain);
        verify(chain).doFilter(any(), any()); verifyNoInteractions(jwt, users);
    }
    @Test void invalidTokenReturns401WithoutContinuing() throws Exception {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer invalid");
        var response = new MockHttpServletResponse(); var chain = mock(FilterChain.class);
        when(jwt.extractUsername("invalid")).thenThrow(new MalformedJwtException("bad"));
        filter.doFilter(request, response, chain);
        assertThat(response.getStatus()).isEqualTo(401); verifyNoInteractions(chain);
    }
    @Test void deletedAccountReturns401() throws Exception {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer token");
        var response = new MockHttpServletResponse();
        when(jwt.extractUsername("token")).thenReturn("missing@example.com");
        when(users.loadUserByUsername("missing@example.com")).thenThrow(new UsernameNotFoundException("missing"));
        filter.doFilter(request, response, mock(FilterChain.class));
        assertThat(response.getStatus()).isEqualTo(401);
    }
    @Test void validTokenPopulatesContextAndContinues() throws Exception {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer token");
        var details = org.springframework.security.core.userdetails.User.withUsername("ada@example.com").password("hash").authorities("ROLE_USER").build();
        when(jwt.extractUsername("token")).thenReturn(details.getUsername());
        when(users.loadUserByUsername(details.getUsername())).thenReturn(details);
        when(jwt.isTokenValid("token", details)).thenReturn(true);
        var chain = mock(FilterChain.class);
        filter.doFilter(request, new MockHttpServletResponse(), chain);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo(details.getUsername());
        verify(chain).doFilter(any(), any());
    }
    @Test void downstreamErrorsAreNotMisclassifiedAsTokenErrors() throws Exception {
        var request = new MockHttpServletRequest(); request.addHeader("Authorization", "Bearer token");
        var chain = mock(FilterChain.class);
        doThrow(new IllegalArgumentException("application error")).when(chain).doFilter(any(), any());
        assertThatThrownBy(() -> filter.doFilter(request, new MockHttpServletResponse(), chain)).isInstanceOf(IllegalArgumentException.class).hasMessage("application error");
    }
}
