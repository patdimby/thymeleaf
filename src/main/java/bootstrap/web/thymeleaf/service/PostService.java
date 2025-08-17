package bootstrap.web.thymeleaf.service;

import bootstrap.web.thymeleaf.dto.PostDto;

import java.util.List;

public interface PostService {
    List<PostDto> findAllPosts();
}
