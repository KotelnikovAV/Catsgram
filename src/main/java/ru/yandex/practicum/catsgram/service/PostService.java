package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.InvalidQueryStringParameter;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static ru.yandex.practicum.catsgram.model.SortOrder.ASCENDING;
import static ru.yandex.practicum.catsgram.model.SortOrder.DESCENDING;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public Collection<Post> findAll(int size, String sort, int from) {
        if (choosingSortingMethod(sort) == null) {
            throw new InvalidQueryStringParameter("Задан неверный способ сортировки: " + sort);
        }
        if (choosingSortingMethod(sort).equals(ASCENDING)) {
            return posts.values().stream()
                    .sorted(Comparator.comparing(Post::getPostDate))
                    .skip(from)
                    .limit(size)
                    .toList();
        } else {
            return posts.values().stream()
                    .sorted((post1, post2) -> -1 * post1.getPostDate().compareTo(post2.getPostDate()))
                    .skip(from)
                    .limit(size)
                    .toList();
        }
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        } else if (!userService.checkIdUser(post.getAuthorId())) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    public Post findPost(long postId) {
        return posts.values().stream()
                .filter(p -> p.getId().equals(postId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пост № %d не найден", postId)));
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private SortOrder choosingSortingMethod (String sort) {
        return switch (sort.toLowerCase()) {
            case "ascending", "asc" -> ASCENDING;
            case "descending", "desc" -> DESCENDING;
            default -> null;
        };
    }
}