package hr.algebra.iisusers.xml;

import hr.algebra.iisusers.users.entity.User;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Uses Jakarta XML Bind (JAXB) + user.xsd for all XML validation.
// validateUser  → validates a single <user> body before saving to DB (Requirement 1)
// validateUsers → validates the generated <users> XML from ReqRes (Requirement 3)
@Service
public class XmlValidationService {

    // Validates a single <user> XML string against user.xsd using JAXB.
    // Returns a list of error messages. Empty list means valid.
    public List<String> validateUser(String xml) {
        return doValidate(xml);
    }

    // Validates the generated <users> XML string against user.xsd using JAXB.
    // Returns a list of error messages. Empty list means valid.
    public List<String> validateUsers(String xml) {
        return doValidate(xml);
    }

    // Shared JAXB validation logic — works for both <user> and <users> root elements.
    private List<String> doValidate(String xml) {
        List<String> errors = new ArrayList<>();
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaUrl = getClass().getClassLoader().getResource("user.xsd");
            Schema schema = sf.newSchema(schemaUrl);

            // Include both JAXB classes so JAXB can resolve either root element.
            JAXBContext context = JAXBContext.newInstance(UserJaxb.class, UsersJaxb.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            // Collect all validation events instead of throwing on the first one.
            unmarshaller.setEventHandler(event -> {
                errors.add(event.getMessage());
                return true;
            });
            unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            Throwable cause = e.getLinkedException() != null ? e.getLinkedException() : e;
            errors.add("XML error: " + cause.getMessage());
        } catch (SAXException e) {
            errors.add("Schema loading error: " + e.getMessage());
        }
        return errors;
    }

    // Parses a valid single-user XML into a User entity ready to be saved.
    // Only call this after validateUser() returns an empty list.
    public User parseToUser(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(UserJaxb.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        UserJaxb jaxb = (UserJaxb) unmarshaller.unmarshal(new StringReader(xml));

        User user = new User();
        user.setEmail(jaxb.getEmail());
        user.setFirstName(jaxb.getFirstName());
        user.setLastName(jaxb.getLastName());
        user.setAvatar(jaxb.getAvatar());
        return user;
    }
}