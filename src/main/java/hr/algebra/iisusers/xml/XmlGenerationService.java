package hr.algebra.iisusers.xml;

import hr.algebra.iisusers.users.dto.ReqResUserDto;
import hr.algebra.iisusers.users.dto.ReqResUsersResponse;
import hr.algebra.iisusers.users.service.UserService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

// Fetches users from the ReqRes public API and builds an XML string.
// The generated XML is stored in memory so the SOAP service and Jakarta XML
// validation endpoint can reuse it without making another HTTP call.
@Service
public class XmlGenerationService {

    private final UserService userService;
    private String lastGeneratedXml;

    public XmlGenerationService(UserService userService) {
        this.userService = userService;
    }

    // Fetches one page of ReqRes users and builds a <users> XML document.
    public String generateFromReqRes(int page) throws Exception {
        ReqResUsersResponse response = userService.getPublicUsers(page);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element root = doc.createElement("users");
        doc.appendChild(root);

        if (response != null && response.getData() != null) {
            for (ReqResUserDto dto : response.getData()) {
                Element userEl = doc.createElement("user");
                addChild(doc, userEl, "id",        String.valueOf(dto.getId()));
                addChild(doc, userEl, "email",     dto.getEmail());
                addChild(doc, userEl, "firstName", dto.getFirstName());
                addChild(doc, userEl, "lastName",  dto.getLastName());
                addChild(doc, userEl, "avatar",    dto.getAvatar());
                root.appendChild(userEl);
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        lastGeneratedXml = writer.toString();
        return lastGeneratedXml;
    }

    // Returns the XML from the most recent generateFromReqRes call.
    // Returns null if generate has not been called yet.

    public String getLastGeneratedXml() {
        return lastGeneratedXml;
    }

    private void addChild(Document doc, Element parent, String name, String value) {
        Element el = doc.createElement(name);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}