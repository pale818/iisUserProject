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

@Service
public class XmlGenerationService {

    private final UserService userService;
    // Holds the most recently generated XML in memory so the SOAP service and Jakarta validator can use it
    private String lastGeneratedXml;
    private boolean validated = false;

    public XmlGenerationService(UserService userService) {
        this.userService = userService;
    }

    // Fetches one page of ReqRes users, builds a DOM tree
    public String generateFromReqRes(int page) throws Exception {
        ReqResUsersResponse response = userService.getPublicUsers(page);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // doc is the Document object — it represents the entire XML file.
        // Every Element must be created through the Document that will own it.


        // root is the <users> element — the single top-level element of the document.
        // doc.createElement() creates the node, doc.appendChild() attaches it as the document root.
        // Tree so far:  doc → <users>
        Element root = doc.createElement("users");
        doc.appendChild(root);

        if (response != null && response.getData() != null) {
            for (ReqResUserDto dto : response.getData()) {
                // userEl is a <user> element — one level below root, one per person.
                // It is created by doc ,but not yet placed in the tree.
                Element userEl = doc.createElement("user");

                // addChild receives doc (to create child elements) and userEl (to attach them to).
                // The parent IS userEl, which is exactly one level below root
                // Tree after each addChild: doc → <users> → <user> → <id>, <email>, ...
                addChild(doc, userEl, "id",        String.valueOf(dto.getId()));
                addChild(doc, userEl, "email",     dto.getEmail());
                addChild(doc, userEl, "firstName", dto.getFirstName());
                addChild(doc, userEl, "lastName",  dto.getLastName());
                addChild(doc, userEl, "avatar",    dto.getAvatar());

                root.appendChild(userEl);
            }
        }

        // Transformer serialises the in-memory DOM tree into an XML string
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));

        lastGeneratedXml = writer.toString();
        validated = false;
        return lastGeneratedXml;
    }

    public String getLastGeneratedXml() {
        return lastGeneratedXml;
    }

    public boolean isValidated() {
        return validated;
    }

    public void markValidated() {
        this.validated = true;
    }

    //parent- one user, name name of the tag (eg. <id>), value - value of the tag(id = xy)
    private void addChild(Document doc, Element parent, String name, String value) {
        Element el = doc.createElement(name);
        el.setTextContent(value);
        parent.appendChild(el);
    }
}
