package bootstrap.web.thymeleaf.mapper;
import bootstrap.web.thymeleaf.dto.PostDto;
import bootstrap.web.thymeleaf.model.Post;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.*;
class PostMapperTests {
    @Test void roundTripPreservesEveryField() {
        Post original = Post.builder().id(7L).title("Title").url("slug").content("Body")
            .shortDescription("Summary").createdOn(LocalDateTime.of(2026,1,1,12,0))
            .updatedOn(LocalDateTime.of(2026,1,2,12,0)).build();
        PostDto dto = PostMapper.mapToPostDto(original);
        assertThat(dto).usingRecursiveComparison().isEqualTo(original);
        assertThat(PostMapper.mapToPost(dto)).usingRecursiveComparison().isEqualTo(original);
    }
    @Test void optionalFieldsRemainNull() {
        PostDto dto = PostMapper.mapToPostDto(Post.builder().title("Title").content("Body").build());
        assertThat(dto.getId()).isNull(); assertThat(dto.getUrl()).isNull();
        assertThat(dto.getCreatedOn()).isNull();
    }
}
