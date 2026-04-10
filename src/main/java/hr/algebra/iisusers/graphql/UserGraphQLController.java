package hr.algebra.iisusers.graphql;

import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class UserGraphQLController {

    private final UserService userService;

    public UserGraphQLController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<User> users() {
        return userService.getAllLocalUsers();
    }

    @QueryMapping
    public User user(@Argument Long id) {
        return userService.getLocalUserById(id);
    }

    @MutationMapping
    public User createUser(@Argument Map<String, String> input) {
        User user = new User();
        user.setEmail(input.get("email"));
        user.setFirstName(input.get("firstName"));
        user.setLastName(input.get("lastName"));
        user.setAvatar(input.getOrDefault("avatar", ""));
        return userService.saveLocalUser(user);
    }

    @MutationMapping
    public String deleteUser(@Argument Long id) {
        userService.deleteLocalUser(id);
        return "User " + id + " deleted";
    }
}
