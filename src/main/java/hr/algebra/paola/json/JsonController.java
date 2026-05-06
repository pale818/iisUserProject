package hr.algebra.paola.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.paola.users.entity.User;
import hr.algebra.paola.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/json")
public class JsonController {

    private final JsonValidationService jsonValidationService;
    private final ObjectMapper objectMapper;
    private final UserService userService;

    public JsonController(JsonValidationService jsonValidationService, ObjectMapper objectMapper, UserService userService) {
        this.jsonValidationService = jsonValidationService;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }



    @PostMapping("/valid-save")
    public ResponseEntity<?> validateAndSave(@RequestBody String json) {
        List<String> errors = jsonValidationService.validate(json);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        try{
            User user = objectMapper.readValue(json,User.class);
            user.setId(null);
            User save = userService.createUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(save);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
