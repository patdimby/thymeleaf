package bootstrap.web.thymeleaf.repository;
import bootstrap.web.thymeleaf.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.*;
@org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase(replace = org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@ActiveProfiles("test")
class RepositoryTests {
    @Autowired PostRepository posts;
    @Autowired UserRepository users;
    @Test void postIsPersistedWithTimestampsAndLookup() {
        Post post = posts.saveAndFlush(Post.builder().title("Article").url("article").content("Content").shortDescription("Summary").build());
        assertThat(post.getId()).isNotNull(); assertThat(post.getCreatedOn()).isNotNull(); assertThat(post.getUpdatedOn()).isNotNull();
        assertThat(posts.findByUrl("article")).isPresent(); assertThat(posts.findByUrl("missing")).isEmpty();
    }
    @Test void userLookupAndRoleRoundTrip() {
        User user = new User(); user.setFirstName("Ada"); user.setUsername("ada"); user.setEmail("ada@example.com"); user.setPassword("hash"); user.setRole(UserRole.ROLE_ADMIN);
        users.saveAndFlush(user);
        assertThat(users.findByEmail(user.getEmail())).get().extracting(User::getRole).isEqualTo(UserRole.ROLE_ADMIN);
        assertThat(users.findByUsername("ada")).isPresent();
        assertThat(users.findByEmail("missing@example.com")).isEmpty();
    }
    @Test void blankUserFieldsFailValidation() {
        User user = new User(); user.setRole(UserRole.ROLE_USER);
        assertThatThrownBy(() -> users.saveAndFlush(user)).isInstanceOf(jakarta.validation.ConstraintViolationException.class);
    }
    @Test void postTitleIsRequired() {
        assertThatThrownBy(() -> posts.saveAndFlush(Post.builder().content("Content").build())).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
