package com.example.spring_rest_intro;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    private  final TagRepository tagRepository;


    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    public void createTag(Tag tag){
        tagRepository.save(tag);
    }

    public void addTagToPost(Long postId, Long tagId){
        Optional<Post> postOptional = postRepository.findById(postId);
        Optional<Tag> tagOptional = tagRepository.findById(tagId);

        if (postOptional.isEmpty() || tagOptional.isEmpty()){
            return;
        }

        Post post = postOptional.get();
        Tag tag = tagOptional.get();

        post.getTags().add(tag);
        postRepository.save(post);


    }

    public void createPost(Post post){
        postRepository.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }
}
