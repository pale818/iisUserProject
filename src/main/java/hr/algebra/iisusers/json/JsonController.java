package hr.algebra.iisusers.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/json")
public class JsonController {

    private final JsonValidationService jsonValidationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public JsonController(JsonValidationService jsonValidationService,
                          UserService userService,
                          ObjectMapper objectMapper) {
        this.jsonValidationService = jsonValidationService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    // Receives a JSON body, validates it against user-schema.json,
    // saves to the database if valid, returns errors if not. (Requirement 1)
    @PostMapping("/validate-save")
    public ResponseEntity<?> validateAndSave(@RequestBody String json) {
        List<String> errors = jsonValidationService.validate(json);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            User user = objectMapper.readValue(json, User.class);
            user.setId(null); // let JPA assign the ID
            User saved = userService.saveLocalUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of(e.getMessage())));
        }
    }
}