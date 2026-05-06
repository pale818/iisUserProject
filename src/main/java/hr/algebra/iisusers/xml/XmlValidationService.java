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

@Service
public class XmlValidationService {

    // Entry point for validating a single <user> element (from the validate-save endpoint)
    public List<String> validateUser(String xml) {
        return doValidate(xml);
    }

    // Entry point for validating a <users> document (from the jakarta-validate endpoint)
    public List<String> validateUsers(String xml) {
        return doValidate(xml);
    }

    private List<String> doValidate(String xml) {
        List<String> errors = new ArrayList<>();
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaUrl = getClass().getClassLoader().getResource("user.xsd");
            Schema schema = sf.newSchema(schemaUrl);

            // Attaching the schema to the Unmarshaller enables validation during unmarshalling (Jakarta XML Bind)
            JAXBContext context = JAXBContext.newInstance(UserJaxb.class, UsersJaxb.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);
            // Return true to keep going after each error — collects all violations instead of stopping at the first
            unmarshaller.setEventHandler(event -> {
                errors.add(event.getMessage());
                return true;
            });
            //validates against schema and turns it to java obj
            unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            Throwable cause = e.getLinkedException() != null ? e.getLinkedException() : e;
            errors.add("XML error: " + cause.getMessage());
        } catch (SAXException e) {
            errors.add("Schema loading error: " + e.getMessage());
        }
        return errors;
    }

    // Parses a single <user> element into a JPA User entity to return it
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
