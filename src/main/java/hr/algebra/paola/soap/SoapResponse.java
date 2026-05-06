package hr.algebra.paola.soap;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "soapResponse", namespace = "http://algebra.hr/paola/soap")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapResponse {


    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
