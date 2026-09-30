package bootstrap.web.thymeleaf.service;
import bootstrap.web.thymeleaf.model.Post;
import bootstrap.web.thymeleaf.repository.PostRepository;
import bootstrap.web.thymeleaf.service.impl.PostServiceImpl;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
class PostServiceTests {
    private final PostRepository repository = mock(PostRepository.class);
    private final PostService service = new PostServiceImpl(repository);
    @Test void mapsRepositoryResultsInOrder() {
        when(repository.findAll()).thenReturn(List.of(Post.builder().id(1L).title("First").build(), Post.builder().id(2L).title("Second").build()));
        assertThat(service.findAllPosts()).extracting("title").containsExactly("First", "Second");
        verify(repository).findAll();
    }
    @Test void supportsEmptyDatabase() {
        when(repository.findAll()).thenReturn(List.of());
        assertThat(service.findAllPosts()).isEmpty();
    }
    @Test void propagatesPersistenceFailure() {
        when(repository.findAll()).thenThrow(new IllegalStateException("database unavailable"));
        assertThatThrownBy(service::findAllPosts).isInstanceOf(IllegalStateException.class);
    }
}
