package hr.algebra.iisusers.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

// JAXB-mapped class representing the incoming SOAP request payload
// Spring WS unmarshals the XML envelope body into this object automatically
@XmlRootElement(name = "searchUsersRequest", namespace = "http://algebra.hr/soap/users")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchUsersRequest {

    private String searchTerm;

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
}