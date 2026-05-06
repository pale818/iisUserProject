package hr.algebra.iisusers.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

// JAXB-mapped class representing the outgoing SOAP response payload
// Spring WS marshals this object into the SOAP envelope body automatically
@XmlRootElement(name = "searchUsersResponse", namespace = "http://algebra.hr/soap/users")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchUsersResponse {

    private String result;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }




}
