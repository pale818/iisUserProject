package hr.algebra.paola.grpc;


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

@Service
public class FetchService {

    private static final String dhmz_url= "https://vrijeme.hr/hrvatska_n.xml";

    private final RestTemplate restTemplate;
    public FetchService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public record WeatherData(String station,String temp, String description){}


    public List<WeatherData> fetchWeather(String station) {
       try {
           byte[] xmlBytes = restTemplate.getForObject(dhmz_url, byte[].class);
           if (xmlBytes == null) {
               return List.of();
           }
           return parseWeather(xmlBytes, station);
       }catch (Exception e){
           return List.of();
       }
    }

    public List<WeatherData>parseWeather(byte[] xmlBytes, String station) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlBytes));

        NodeList nodes = doc.getElementsByTagName("Grad");
        List<WeatherData> matches = new ArrayList<>();
        Element first = null;

        for(int i = 0; i < nodes.getLength(); i++){
            Element element = (Element) nodes.item(i);
            if(first == null) first = element;
            String name = element.getElementsByTagName("GradIme").item(0).getTextContent();
            if(name.toLowerCase().contains(station.toLowerCase())){
                matches.add(buildWeatherData(element));
            }
        }

        return matches;

    }


    private WeatherData buildWeatherData(Element element){
        Element  podatci =  (Element) element.getElementsByTagName("Podatci").item(0);
        if ( podatci == null) return null;
        String station = element.getElementsByTagName("GradIme").item(0).getTextContent();
        String temp = podatci.getElementsByTagName("Temp").item(0).getTextContent();
        String description = podatci.getElementsByTagName("Vrijeme").item(0).getTextContent();

        return new WeatherData(station,temp,description);
    }

}
