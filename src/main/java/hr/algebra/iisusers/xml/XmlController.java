package hr.algebra.iisusers.xml;

import hr.algebra.iisusers.users.entity.User;
import hr.algebra.iisusers.users.service.UserService;
import jakarta.xml.bind.JAXBException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/xml")
public class XmlController {

    private final XmlGenerationService xmlGenerationService;
    private final XmlValidationService xmlValidationService;
    private final UserService userService;

    public XmlController(XmlGenerationService xmlGenerationService,
                         XmlValidationService xmlValidationService,
                         UserService userService) {
        this.xmlGenerationService = xmlGenerationService;
        this.xmlValidationService = xmlValidationService;
        this.userService = userService;
    }

    // Fetches ReqRes users and returns them as XML.
    // Also stores the result so SOAP search and Jakarta validate can reuse it.
    @GetMapping(value = "/generate", produces = MediaType.TEXT_XML_VALUE)
    public String generate(@RequestParam(defaultValue = "1") int page) throws Exception {
        return xmlGenerationService.generateFromReqRes(page);
    }

    // Receives a single <user> XML body, validates it with JAXB + XSD,
    // saves to the database if valid, returns errors if not. (Requirement 1)
    @PostMapping("/validate-save")
    public ResponseEntity<?> validateAndSave(@RequestBody String xml) {
        List<String> errors = xmlValidationService.validateUser(xml);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }
        try {
            User user = xmlValidationService.parseToUser(xml);
            User saved = userService.saveLocalUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (JAXBException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of(e.getMessage())));
        }
    }

    // Validates the last generatework wit XML using Jakarta XML Bind (JAXB) + XSD.
    // Call GET /api/xml/generate first to populate the stored XML. (Requirement 3)
    @GetMapping("/jakarta-validate")
    public ResponseEntity<?> jakartaValidate() {
        String xml = xmlGenerationService.getLastGeneratedXml();
        if (xml == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No XML generated yet. Call GET /api/xml/generate first."));
        }
        List<String> errors = xmlValidationService.validateUsers(xml);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "Generated XML is valid according to user.xsd"));
        }
        return ResponseEntity.ok(Map.of("errors", errors));
    }
}