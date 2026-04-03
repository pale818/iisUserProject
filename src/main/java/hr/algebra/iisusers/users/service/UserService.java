package hr.algebra.iisusers.users.service;

import hr.algebra.iisusers.users.dto.ReqResUsersResponse;
import hr.algebra.iisusers.users.dto.ReqResUserDto;
import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RestClient restClient;
    private final String reqresApiKey;

    public UserService(
            UserRepository userRepository,
            RestClient.Builder restClientBuilder,
            @Value("${reqres.api-key}") String reqresApiKey
    ) {
        this.userRepository = userRepository;
        this.restClient = restClientBuilder.baseUrl("https://reqres.in").build();
        this.reqresApiKey = reqresApiKey;
    }

    // Local users are stored in H2 and will become the base for the custom API.
    public List<User> getAllLocalUsers() {
        return userRepository.findAll();
    }

    // This simple save method is enough for Day 1 and Day 2 CRUD work.
    public User saveLocalUser(User user) {
        return userRepository.save(user);
    }

    // This helper imports one ReqRes page into the local H2 database.
    public List<User> importPublicUsers(int page) {
        ReqResUsersResponse response = getPublicUsers(page);
        List<User> usersToSave = new ArrayList<>();

        if (response == null || response.getData() == null) {
            return usersToSave;
        }

        for (ReqResUserDto publicUser : response.getData()) {
            User localUser = new User();
            localUser.setEmail(publicUser.getEmail());
            localUser.setFirstName(publicUser.getFirstName());
            localUser.setLastName(publicUser.getLastName());
            localUser.setAvatar(publicUser.getAvatar());
            usersToSave.add(localUser);
        }

        return userRepository.saveAll(usersToSave);
    }

    // Fetch a page of users from ReqRes so we can compare the public and local models.
    public ReqResUsersResponse getPublicUsers(int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/users").queryParam("page", page).build())
                .header("x-api-key", reqresApiKey)
                .retrieve()
                .body(ReqResUsersResponse.class);
    }
}
