package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();
    private final List<String> emails = new ArrayList<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (emails.contains(user.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        emails.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (emails.contains(newUser.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                oldUser.setPassword(newUser.getPassword());
                oldUser.setUsername(newUser.getUsername());
            } else if (newUser.getPassword()== null || newUser.getPassword().isBlank()) {
                oldUser.setUsername(newUser.getUsername());
                oldUser.setEmail(newUser.getEmail());
            } else if (newUser.getUsername()== null || newUser.getUsername().isBlank()) {
                oldUser.setEmail(newUser.getEmail());
                oldUser.setPassword(newUser.getPassword());
            } else {
                oldUser.setEmail(newUser.getEmail());
                oldUser.setPassword(newUser.getPassword());
                oldUser.setUsername(newUser.getUsername());
            }
            return oldUser;
        } else {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
    }

    public User findPost(long userId) {
        return users.values().stream()
                .filter(p -> p.getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь № %d не найден", userId)));
    }

    public boolean checkIdUser(long id) {
        return users.containsKey(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
