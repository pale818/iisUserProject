package hr.algebra.iisusers.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

// Wrapper class for unmarshalling a <users> document containing multiple <user> elements
@XmlRootElement(name = "users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersJaxb {

    // @XmlElement maps each <user> child element to an item in this list
    @XmlElement(name = "user")
    private List<UserJaxb> users = new ArrayList<>();

    public List<UserJaxb> getUsers() { return users; }
    public void setUsers(List<UserJaxb> users) { this.users = users; }
}
