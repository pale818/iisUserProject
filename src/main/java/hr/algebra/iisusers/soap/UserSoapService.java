package hr.algebra.iisusers.soap;

import hr.algebra.iisusers.xml.XmlGenerationService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.io.StringWriter;

@Endpoint
public class UserSoapService {

    //unique namespace
    private static final String NAMESPACE = "http://algebra.hr/soap/users";

    private final XmlGenerationService xmlGenerationService;

    public UserSoapService(XmlGenerationService xmlGenerationService) {
        this.xmlGenerationService = xmlGenerationService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "searchUsersRequest")
    @ResponsePayload
    public SearchUsersResponse search(@RequestPayload SearchUsersRequest request) throws Exception {
        // XML must be generated first via GET /api/xml/generate
        String xml = xmlGenerationService.getLastGeneratedXml();
        if (xml == null) {
            SearchUsersResponse error = new SearchUsersResponse();
            error.setResult("Error: XML has not been generated yet. Please click 'Generate XML from ReqRes' first.");
            return error;
        }
        //must be validated firts as well
        if (!xmlGenerationService.isValidated()) {
            SearchUsersResponse error = new SearchUsersResponse();
            error.setResult("Error: XML has not been validated yet. Please click 'Validate XML (Jakarta)' first.");
            return error;
        }

        //parses the xml
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));


        //xpath used to search the document(has all public users) by user input(eaither firstame or lastname)
        String term = request.getSearchTerm().toLowerCase();
        XPath xpath = XPathFactory.newInstance().newXPath();
        // translate() converts firstName/lastName to lowercase before comparing)
        String expression = "//user[contains(translate(firstName,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + term + "')" +
                " or contains(translate(lastName,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + term + "')]";
        NodeList matched = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);


        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        // Single StringWriter reused across all nodes — transformer writes directly into its buffer
        StringWriter sw = new StringWriter();
        sw.write("<matchedUsers>");

        //transformes the doc obj to text, filled in sw
        for (int i = 0; i < matched.getLength(); i++) {
            Node node = matched.item(i);
            transformer.transform(new DOMSource(node), new StreamResult(sw));
        }

        sw.write("</matchedUsers>");

        //returned as string
        SearchUsersResponse response = new SearchUsersResponse();
        response.setResult(sw.toString());
        return response;
    }
}
