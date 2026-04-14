package hr.algebra.iisusers.grpc;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches the live DHMZ Croatian weather XML and extracts data for all stations
 * matching the given name (partial, case-insensitive).
 * DHMZ XML URL: http://vrijeme.hr/hrvatska_n.xml
 */
@Service
public class WeatherFetchService {

    private static final String DHMZ_URL = "http://vrijeme.hr/hrvatska_n.xml";

    private final RestTemplate restTemplate = new RestTemplate();

    // Returns all stations whose name contains the requested string (case-insensitive).
    // Falls back to the first station if nothing matches.
    public List<WeatherData> fetchWeather(String stationName) {
        try {
            byte[] xmlBytes = restTemplate.getForObject(DHMZ_URL, byte[].class);
            if (xmlBytes == null || xmlBytes.length == 0) {
                return List.of(new WeatherData(stationName, "N/A", "Could not fetch data", "N/A"));
            }
            return parseWeather(xmlBytes, stationName);
        } catch (Exception e) {
            return List.of(new WeatherData(stationName, "N/A", "Error: " + e.getMessage(), "N/A"));
        }
    }

    private List<WeatherData> parseWeather(byte[] xmlBytes, String requestedStation) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlBytes));

        String datum = rootText(doc, "Datum");
        String termin = rootText(doc, "Termin");
        String timestamp = datum + " " + termin + ":00";

        NodeList stations = doc.getElementsByTagName("Grad");
        List<WeatherData> matches = new ArrayList<>();
        Element first = null;

        for (int i = 0; i < stations.getLength(); i++) {
            Element el = (Element) stations.item(i);
            if (first == null) first = el;
            String name = directText(el, "GradIme");
            if (name.toLowerCase().contains(requestedStation.toLowerCase())) {
                matches.add(buildWeatherData(el, timestamp));
            }
        }

        // Fall back to first station if nothing matched
        if (matches.isEmpty() && first != null) {
            matches.add(buildWeatherData(first, timestamp));
        }

        return matches;
    }

    private WeatherData buildWeatherData(Element station, String timestamp) {
        String name = directText(station, "GradIme");
        String temp = "N/A";
        String desc = "N/A";

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
