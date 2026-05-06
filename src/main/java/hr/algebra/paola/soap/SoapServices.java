package hr.algebra.paola.soap;


import hr.algebra.paola.xml.XmlGen;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
public class SoapServices {

    private final static String namespaceUrl= "http://algebra.hr/paola/soap";


    private final XmlGen xmlGen;
    public SoapServices(XmlGen xmlGen) {
        this.xmlGen = xmlGen;
    }

    @PayloadRoot(namespace = namespaceUrl, localPart = "soapRequest")
    @ResponsePayload
    public SoapResponse searchUsers(SoapRequest request) throws Exception {
        String xml = xmlGen.getLastXml();
        if(xml==null){
            xmlGen.generateXml(1);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new InputSource(new StringReader(xml)));

        String term = request.getSeachTerm().toLowerCase();
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = "//user[contains(translate(firstName,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + term + "')" +
                " or contains(translate(lastName,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + term + "')]";
        NodeList match = (NodeList) xpath.evaluate(expression,doc, XPathConstants.NODESET);


        Transformer tf = TransformerFactory.newInstance().newTransformer();
        StringBuilder sb = new StringBuilder("<matchedUsers>");
        for (int i = 0; i < match.getLength(); i++) {
            Node item = match.item(i);
            StringWriter sw = new StringWriter();
            tf.transform(new DOMSource(doc),new StreamResult(sw));
            sb.append(sw.toString());
        }


        SoapResponse response = new SoapResponse();
        response.setResponse(sb.toString());
        return response;
    }




}
