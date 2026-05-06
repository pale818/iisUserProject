package hr.algebra.paola.graphQL;


import hr.algebra.paola.users.service.UserService;
import hr.algebra.paola.users.entity.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
public class GraphController {

    private final UserService userService;
    public GraphController(UserService userService) {
        this.userService = userService;
    }


    @QueryMapping
    public List<User> listUsers() { return  userService.getLocalUsers();}

    @QueryMapping
    public User getUser(@Argument Long id) { return userService.getUser(id);}

    @MutationMapping
    public User createUser(@Argument Map<String,String> input ) {
        User user = new User();
        user.setFirstName(input.get("firstName"));
        user.setEmail(input.get("email"));
        user.setLastName(input.get("lastName"));
        return userService.createUser(user);
    }


    @MutationMapping
    public User updateUser(@Argument long id,@Argument Map<String,String> input ) {
        User user = new User();
        user.setFirstName(input.get("firstName"));
        user.setEmail(input.get("email"));
        user.setLastName(input.get("lastName"));
        return userService.updateUser(id,user);
    }

    @MutationMapping
    public void deleteUser(@Argument long id) {
         userService.deleteUser(id);
    }


}
