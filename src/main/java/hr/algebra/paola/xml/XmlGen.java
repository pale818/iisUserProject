package hr.algebra.paola.xml;


import hr.algebra.paola.users.dto.ReqResDto;
import hr.algebra.paola.users.dto.ReqResResponse;
import hr.algebra.paola.users.service.UserService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Service
public class XmlGen {

    private String lastXml;
    private final UserService userService;
    public  XmlGen(UserService userService) {
        this.userService = userService;
    }


    public String generateXml(int page) throws Exception {
        ReqResResponse response = userService.getPublicUsers(page);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();


        Element root = doc.createElement("users");
        doc.appendChild(root);

        if(response != null && response.getReqResDtos() != null) {
            for (ReqResDto dto : response.getReqResDtos()) {
                Element userEl = doc.createElement("user");
                AddChild(doc,userEl,"email",dto.getEmail());
                AddChild(doc,userEl,"firstName",dto.getFirstName());
                AddChild(doc,userEl,"lastName",dto.getLastName());
                root.appendChild(userEl);
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc),new StreamResult(writer));
        lastXml= writer.toString();
        return lastXml;

    }

    public String getLastXml() { return lastXml;}


    private void AddChild(Document doc, Element userEl, String name, String value) {
        Element child = doc.createElement(name);
        child.appendChild(userEl);
        userEl.appendChild(child);
    }

}
