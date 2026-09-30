package bootstrap.web.thymeleaf.controller;

import bootstrap.web.thymeleaf.dto.PostDto;
import bootstrap.web.thymeleaf.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Expose DTOs to the template without leaking JPA entities into the view layer.
    @GetMapping("/admin/posts")
    public String posts(Model model){
        List<PostDto> posts = postService.findAllPosts();
        model.addAttribute("posts", posts);
        return "admin/posts";
    }

}
