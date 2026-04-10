package hr.algebra.iisusers.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

// JAXB-annotated class that mirrors the <users> element in user.xsd.
// Used by XmlValidationService to validate the generated ReqRes XML (Jakarta XML requirement).
@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersJaxb {

    @XmlElement(name = "user")
    private List<UserJaxb> users = new ArrayList<>();

    public List<UserJaxb> getUsers() { return users; }
    public void setUsers(List<UserJaxb> users) { this.users = users; }
}