package hr.algebra.iisusers.users.controller;

import hr.algebra.iisusers.users.dto.ReqResUsersResponse;
import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    // Return one local user by database id.
    @GetMapping("/{id}")
    public User getLocalUserById(@PathVariable Long id) {
        return userService.getLocalUserById(id);
    }

    // Save a local user so we can start building our custom CRUD API.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createLocalUser(@RequestBody User user) {
        return userService.saveLocalUser(user);
    }

    // Update one local user using the same simple request body shape as POST.
    @PutMapping("/{id}")
    public User updateLocalUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateLocalUser(id, user);
    }

    // Delete one local user and return a short confirmation message.
    @DeleteMapping("/{id}")
    public Map<String, String> deleteLocalUser(@PathVariable Long id) {
        userService.deleteLocalUser(id);
        return Map.of("message", "User deleted successfully.");
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
