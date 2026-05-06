package hr.algebra.paola.users.service;

import hr.algebra.paola.users.dto.ReqResDto;
import hr.algebra.paola.users.dto.ReqResResponse;
import hr.algebra.paola.users.entity.User;
import hr.algebra.paola.users.repo.Repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    private final Repository repository;
    private final RestClient restClient;
    private final String apiKey;

    public UserService(Repository repository,
                       RestClient.Builder restClientBuilder,
                       @Value("${reqres.api-key}") String apiKey) {
        this.repository = repository;
        this.restClient = restClientBuilder.baseUrl("https://reqres.in").build();
        this.apiKey = apiKey;
    }

    public List<User> getLocalUsers() {
        return this.repository.findAll();
    }

    public ReqResResponse getPublicUsers(int page){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/users").queryParam("page",page).build())
                .header("x-api-key",apiKey)
                .retrieve()
                .body(ReqResResponse.class);
    }


    public User getUser(Long id){
        return repository.findById(id)
                .orElseThrow();
    }

    public User createUser(User user) {
        return repository.save(user);
    }

    public User updateUser(Long id, User user) {
        User u = repository.findById(id).orElseThrow();
        u.setFirstName(user.getFirstName());
        u.setLastName(user.getLastName());
        u.setEmail(user.getEmail());
        u.setAvatar(user.getAvatar());

        return repository.save(u);
    }


    public void deleteUser(Long id) {
        repository.deleteById(id);
    }


    public List<User> importPublic(int page){
        ReqResResponse response = getPublicUsers(page);
        List<User> fetchedUser = new ArrayList<>();

        if(response == null ){
            return fetchedUser;
        }
        for(ReqResDto dto : response.getReqResDtos()){
            User localUser = new User();
            localUser.setEmail(dto.getEmail());
            localUser.setFirstName(dto.getFirstName());
            localUser.setLastName(dto.getLastName());
            localUser.setAvatar(dto.getAvatar());
            fetchedUser.add(localUser);
        }
        return fetchedUser;
    }


}
