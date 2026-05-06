package hr.algebra.paola.xml;

import hr.algebra.iisusers.xml.UserJaxb;
import hr.algebra.paola.users.entity.User;
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
public class XmlValid {


    public List<String> validateXml (String xml) {

        List<String>errors  = new ArrayList<>();
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaUrl = getClass().getClassLoader().getResource("/xsd/valid.xsd");
            Schema schema = factory.newSchema(schemaUrl);

            JAXBContext jaxbContext = JAXBContext.newInstance(UserJaxb.class,UsersJaxb.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(schema);
            unmarshaller.unmarshal(new StringReader(xml));
        } catch (SAXException e) {
            errors.add(e.getMessage());
        } catch (JAXBException e) {
            errors.add(e.getMessage());
        }
        return errors;
    }


    public User parseXml(String xml) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(UserJaxb.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        UserJaxb userJaxb = (UserJaxb) unmarshaller.unmarshal(new StringReader(xml));

        User user = new User();
        user.setEmail(userJaxb.getEmail());
        user.setFirstName(userJaxb.getFirstName());
        user.setLastName(userJaxb.getLastName());
        return user;
    }



}
