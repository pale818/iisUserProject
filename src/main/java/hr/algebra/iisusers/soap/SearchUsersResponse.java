package hr.algebra.iisusers.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchUsersResponse", namespace = "http://algebra.hr/soap/users")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchUsersResponse {

    // Contains an XML string of the matched <user> elements.
    private String result;

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
}