package hr.algebra.iisusers.users.controller;

import hr.algebra.iisusers.users.dto.ReqResUsersResponse;
import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Return users stored in the local H2 database.
    @GetMapping
    public List<User> getAllLocalUsers() {
        return userService.getAllLocalUsers();
    }

    // Save a local user so we can start building our custom CRUD API.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createLocalUser(@RequestBody User user) {
        return userService.saveLocalUser(user);
    }

    // This temporary helper imports public ReqRes users into the local H2 database.
    @PostMapping("/import-public")
    public List<User> importPublicUsers(@RequestParam(defaultValue = "1") int page) {
        return userService.importPublicUsers(page);
    }

    // Expose the public ReqRes response directly for investigation during Day 1.
    @GetMapping("/public")
    public ReqResUsersResponse getPublicUsers(@RequestParam(defaultValue = "1") int page) {
        return userService.getPublicUsers(page);
    }
}
