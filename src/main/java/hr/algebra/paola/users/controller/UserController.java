package hr.algebra.paola.users.controller;


import hr.algebra.paola.users.dto.ReqResResponse;
import hr.algebra.paola.users.entity.User;
import hr.algebra.paola.users.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;

    }


    @GetMapping
    public List<User> getLocalUsers() {
        return userService.getLocalUsers();
    }

    @GetMapping("/public")
    public ReqResResponse getPublicUsers(@RequestParam(defaultValue="1") int page) {
        return userService.getPublicUsers(page);
    }


    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }


    @PostMapping
        public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
         userService.deleteUser(id);
    }

    @PostMapping("/import")
    public List<User> importPublic(@RequestParam(defaultValue = "1")int page) {
        return userService.importPublic(page);
    }


}
