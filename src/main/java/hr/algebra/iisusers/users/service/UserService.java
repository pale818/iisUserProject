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
            RestClient.Builder restClientBuilder, // Spring auto-configures the builder with defaults
            @Value("${reqres.api-key}") String reqresApiKey
    ) {
        this.userRepository = userRepository;
        this.restClient = restClientBuilder.baseUrl("https://reqres.in").build();
        this.reqresApiKey = reqresApiKey;
    }

    public List<User> getAllLocalUsers() {
        return userRepository.findAll();
    }

    public User getLocalUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public User saveLocalUser(User user) {
        return userRepository.save(user);
    }

    // Partial update — only overwrites fields that are present and non-blank in the request
    public User updateLocalUser(Long id, User updatedUser) {
        User existingUser = getLocalUserById(id);
        if (updatedUser.getEmail()     != null && !updatedUser.getEmail().isBlank())     existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isBlank()) existingUser.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName()  != null && !updatedUser.getLastName().isBlank())  existingUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getAvatar()    != null && !updatedUser.getAvatar().isBlank())    existingUser.setAvatar(updatedUser.getAvatar());
        return userRepository.save(existingUser);
    }

    public void deleteLocalUser(Long id) {
        User existingUser = getLocalUserById(id);
        userRepository.delete(existingUser);
    }

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

    // Calls the ReqRes public REST API and deserializes the paginated response
    public ReqResUsersResponse getPublicUsers(int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/users").queryParam("page", page).build())
                .header("x-api-key", reqresApiKey)
                .retrieve()
                .body(ReqResUsersResponse.class);
    }
}
