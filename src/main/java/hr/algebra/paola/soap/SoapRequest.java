package hr.algebra.paola.soap;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "soapRequest", namespace = "http://algebra.hr/paola/soap")
@XmlAccessorType(XmlAccessType.FIELD)
public class SoapRequest {


    private String seachTerm;

    public String getSeachTerm() {
        return seachTerm;
    }

    public void setSeachTerm(String seachTerm) {
        this.seachTerm = seachTerm;
    }
}
