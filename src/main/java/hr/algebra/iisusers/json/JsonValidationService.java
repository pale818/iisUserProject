package hr.algebra.iisusers.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsonValidationService {

    private final JsonSchema schema;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonValidationService() {
        try {
            // JSON Schema Draft-07 — matches the $schema declared in user-schema.json
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
            InputStream stream = getClass().getClassLoader().getResourceAsStream("user-schema.json");
            this.schema = factory.getSchema(stream);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load user-schema.json from classpath", e);
        }
    }

    public List<String> validate(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            Set<ValidationMessage> errors = schema.validate(node);
            // Each ValidationMessage describes one constraint violation (missing field, wrong type, etc.)
            return errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return List.of("Invalid JSON: " + e.getMessage());
        }
    }
}
