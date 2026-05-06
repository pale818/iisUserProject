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

    @GetMapping
    public List<User> getAllLocalUsers() {
        return userService.getAllLocalUsers();
    }

    @GetMapping("/{id}")
    public User getLocalUserById(@PathVariable Long id) {
        return userService.getLocalUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // returns 201 instead of default 200
    public User createLocalUser(@RequestBody User user) {
        return userService.saveLocalUser(user);
    }


    @PutMapping("/{id}")
    public User updateLocalUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateLocalUser(id, user);
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteLocalUser(@PathVariable Long id) {
        userService.deleteLocalUser(id);
        return Map.of("message", "User deleted successfully.");
    }

    // Fetches users from ReqRes and saves them to the local H2 database
    @GetMapping("/import-public")
    public List<User> importPublicUsers(@RequestParam(defaultValue = "1") int page) {
        return userService.importPublicUsers(page);
    }

    //Passes the ReqRes response directly to the client without saving — live public data
    @GetMapping("/public")
    public ReqResUsersResponse getPublicUsers(@RequestParam(defaultValue = "1") int page) {
        return userService.getPublicUsers(page);
    }
}
