package ru.yandex.practicum.catsgram.exception;

public class InvalidQueryStringParameter extends RuntimeException {
    public InvalidQueryStringParameter(String message) {
        super(message);
    }
}