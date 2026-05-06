package hr.algebra.paola.xml;

import hr.algebra.paola.users.service.UserService;
import hr.algebra.iisusers.xml.XmlGenerationService;
import hr.algebra.iisusers.xml.XmlValidationService;
import hr.algebra.paola.users.entity.User;
import jakarta.xml.bind.JAXBException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xml")
public class XmlController {

    private final UserService userService;
    private final XmlGen xmlGen;
    private final XmlValid xmlValid;

    public XmlController(UserService userService, XmlGen xmlGen, XmlValid xmlValid) {
        this.userService = userService;
        this.xmlGen = xmlGen;
        this.xmlValid = xmlValid;
    }

    @PostMapping("/valid-save")
    public ResponseEntity<?> saveXml(@RequestBody String xml) throws JAXBException {
        List<String> error = xmlValid.validateXml(xml);
        if (!error.isEmpty()) {
            return ResponseEntity.badRequest().body("error");
        }
        User user = xmlValid.parseXml(xml);
        User saved = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }










}
