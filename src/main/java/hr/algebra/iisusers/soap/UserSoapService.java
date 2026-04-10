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

// SOAP endpoint: accepts a search term, filters the generated ReqRes XML with XPath,
// and returns the matching <user> nodes as an XML string.
@Endpoint
public class UserSoapService {

    private static final String NAMESPACE = "http://algebra.hr/soap/users";

    private final XmlGenerationService xmlGenerationService;

    public UserSoapService(XmlGenerationService xmlGenerationService) {
        this.xmlGenerationService = xmlGenerationService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "searchUsersRequest")
    @ResponsePayload
    public SearchUsersResponse search(@RequestPayload SearchUsersRequest request) throws Exception {
        // Use the stored XML, or generate a fresh one from page 1 if none exists yet.
        String xml = xmlGenerationService.getLastGeneratedXml();
        if (xml == null) {
            xml = xmlGenerationService.generateFromReqRes(1);
        }

        // Parse the XML document.
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));

        // Use XPath to find all <user> elements where firstName or lastName contains the search term.
        String term = request.getSearchTerm();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "//user[contains(firstName,'" + term + "') or contains(lastName,'" + term + "')]";
        NodeList matched = (NodeList) xpath.evaluate(expression, doc, XPathConstants.NODESET);

        // Serialize the matched nodes back to an XML string.
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringBuilder sb = new StringBuilder("<matchedUsers>");
        for (int i = 0; i < matched.getLength(); i++) {
            Node node = matched.item(i);
            StringWriter sw = new StringWriter();
            transformer.transform(new DOMSource(node), new StreamResult(sw));
            sb.append(sw.toString());
        }
        sb.append("</matchedUsers>");

        SearchUsersResponse response = new SearchUsersResponse();
        response.setResult(sb.toString());
        return response;
    }
}