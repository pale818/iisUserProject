package hr.algebra.iisusers.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// @RestControllerAdvice catches exceptions from any @RestController and returns JSON error responses
@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // returns 404 instead of the default 500
    public Map<String, String> handleUserNotFound(UserNotFoundException exception) {
        return Map.of("message", exception.getMessage());
    }
}
