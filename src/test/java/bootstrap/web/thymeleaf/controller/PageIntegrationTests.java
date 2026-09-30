package bootstrap.web.thymeleaf.controller;
import bootstrap.web.thymeleaf.model.Post;
import bootstrap.web.thymeleaf.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PageIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired PostRepository posts;
    @BeforeEach void reset() { posts.deleteAll(); }
    @Test void emptyPostsPageRenders() throws Exception {
        mvc.perform(get("/admin/posts")).andExpect(status().isOk()).andExpect(view().name("admin/posts"))
            .andExpect(model().attributeExists("posts")).andExpect(content().string(containsString("No posts available yet.")));
    }
    @Test void postsAreRenderedAndEscaped() throws Exception {
        posts.saveAndFlush(Post.builder().title("<script>alert(1)</script>").content("Body").shortDescription("Summary").build());
        mvc.perform(get("/admin/posts")).andExpect(status().isOk())
            .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")))
            .andExpect(content().string(containsString("Summary")));
    }
    @Test void homeControllerIsRegistered() throws Exception {
        mvc.perform(get("/admin/index")).andExpect(status().isOk()).andExpect(view().name("admin/index"));
    }
    @Test void staticStylesheetIsPublic() throws Exception {
        mvc.perform(get("/css/bootstrap.min.css")).andExpect(status().isOk());
    }
    @Test void unknownProtectedRouteIsNotPublic() throws Exception {
        mvc.perform(get("/private")).andExpect(status().isForbidden());
    }
    @Test void malformedBearerTokenIsRejected() throws Exception {
        mvc.perform(get("/admin/posts").header("Authorization", "Bearer garbage")).andExpect(status().isUnauthorized());
    }
}
