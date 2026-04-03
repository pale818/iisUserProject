package hr.algebra.iisusers.users.service;

import hr.algebra.iisusers.users.dto.ReqResUsersResponse;
import hr.algebra.iisusers.users.dto.ReqResUserDto;
import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.exception.UserNotFoundException;
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

    // Return one local user or fail with a simple 404-style exception.
    public User getLocalUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    // This simple save method is enough for Day 1 and Day 2 CRUD work.
    public User saveLocalUser(User user) {
        return userRepository.save(user);
    }

    // Update only the fields we currently keep in the local user model.
    public User updateLocalUser(Long id, User updatedUser) {
        User existingUser = getLocalUserById(id);
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setAvatar(updatedUser.getAvatar());
        return userRepository.save(existingUser);
    }

    // Delete one user after checking that the record exists.
    public void deleteLocalUser(Long id) {
        User existingUser = getLocalUserById(id);
        userRepository.delete(existingUser);
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
