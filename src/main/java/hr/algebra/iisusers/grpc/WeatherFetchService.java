package hr.algebra.iisusers.grpc;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Fetches the live DHMZ Croatian weather XML and extracts data for a given station.
 * DHMZ XML URL: http://vrijeme.hr/hrvatska_n.xml
 *
 * XML structure:
 *   <Hrvatska>
 *     <DatumTermin><Datum>09.04.2026</Datum><Termin>17</Termin></DatumTermin>
 *     <Grad>
 *       <GradIme>Zagreb-Maksimir</GradIme>
 *       <Podatci><Temp>14.2</Temp><Vrijeme>umjereno oblačno</Vrijeme>...</Podatci>
 *     </Grad>
 *   </Hrvatska>
 *
 * Station name matching is case-insensitive and partial (e.g. "zagreb" matches "Zagreb-Maksimir").
 */
@Service
public class WeatherFetchService {

    private static final String DHMZ_URL = "http://vrijeme.hr/hrvatska_n.xml";

    private final RestTemplate restTemplate = new RestTemplate();

    public WeatherData fetchWeather(String stationName) {
        try {
            // Fetch as raw bytes so DocumentBuilder reads the XML encoding declaration correctly
            byte[] xmlBytes = restTemplate.getForObject(DHMZ_URL, byte[].class);
            if (xmlBytes == null || xmlBytes.length == 0) {
                return new WeatherData(stationName, "N/A", "Could not fetch data", "N/A");
            }
            return parseWeather(xmlBytes, stationName);
        } catch (Exception e) {
            return new WeatherData(stationName, "N/A", "Error: " + e.getMessage(), "N/A");
        }
    }

    private WeatherData parseWeather(byte[] xmlBytes, String requestedStation) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlBytes));

        // Date and time are at the document root inside <DatumTermin>
        String datum = rootText(doc, "Datum");
        String termin = rootText(doc, "Termin");
        String timestamp = datum + " " + termin + ":00";

        NodeList stations = doc.getElementsByTagName("Grad");
        Element match = null;
        Element first = null;

        for (int i = 0; i < stations.getLength(); i++) {
            Element el = (Element) stations.item(i);
            if (first == null) first = el;
            String name = directText(el, "GradIme");
            // Partial, case-insensitive match so "zagreb" finds "Zagreb-Maksimir"
            if (name.toLowerCase().contains(requestedStation.toLowerCase())) {
                match = el;
                break;
            }
        }

        Element station = (match != null) ? match : first;
        if (station == null) {
            return new WeatherData(requestedStation, "N/A", "No station data found", timestamp);
        }

        String name = directText(station, "GradIme");
        String temp = "N/A";
        String desc = "N/A";

        // Weather readings are nested inside <Podatci>
        NodeList podatci = station.getElementsByTagName("Podatci");
        if (podatci.getLength() > 0) {
            Element p = (Element) podatci.item(0);
            temp = directText(p, "Temp").trim() + " °C";
            desc = directText(p, "Vrijeme");
        }

        return new WeatherData(name, temp, desc, timestamp);
    }

    private String rootText(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "N/A";
        String val = nodes.item(0).getTextContent();
        return (val == null || val.isBlank()) ? "N/A" : val.trim();
    }

    private String directText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        if (nodes.getLength() == 0) return "N/A";
        String val = nodes.item(0).getTextContent();
        return (val == null || val.isBlank()) ? "N/A" : val.trim();
    }

    public record WeatherData(String station, String temperature, String description, String timestamp) {}
}