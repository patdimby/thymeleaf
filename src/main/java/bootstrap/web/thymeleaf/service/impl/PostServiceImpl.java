package bootstrap.web.thymeleaf.service.impl;


import bootstrap.web.thymeleaf.dto.PostDto;
import bootstrap.web.thymeleaf.mapper.PostMapper;
import bootstrap.web.thymeleaf.model.Post;
import bootstrap.web.thymeleaf.repository.PostRepository;
import bootstrap.web.thymeleaf.service.PostService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public List<PostDto> findAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostMapper::mapToPostDto)
                .collect(Collectors.toList());
    }
}
