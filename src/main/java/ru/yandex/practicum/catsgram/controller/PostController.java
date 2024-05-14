package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(@RequestParam Optional<Integer> size,
                                    @RequestParam Optional<String> sort,
                                    @RequestParam Optional<Integer> from) {
        if (size.isEmpty() || sort.isEmpty() || from.isEmpty()) {
            return postService.findAll();
        } else {
            return postService.findAll(size.get(), sort.get(), from.get());
        }
    }

    @GetMapping("/{postId}")
    public Post findPost(@PathVariable long postId) {
        return postService.findPost(postId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}