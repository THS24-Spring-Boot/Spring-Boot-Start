package com.example.spring_rest_intro;


import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;


    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public void createPost(@RequestBody Post post){
        postService.createPost(post);
    }

    @PutMapping("/tag")
    public void addTagToPost(@RequestParam Long postId, @RequestParam Long tagId ){
        postService.addTagToPost(postId,tagId);
    }

    @GetMapping
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }

    @PostMapping("/tag")
    public void createNewTag(@RequestBody Tag tag){
        postService.createTag(tag);
    }




}
