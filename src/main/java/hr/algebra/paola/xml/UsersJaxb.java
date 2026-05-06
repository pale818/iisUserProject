package hr.algebra.paola.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

@XmlRootElement(name="users")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersJaxb {

    @XmlElement(name= "user")
    private List<UserJaxb> users;

    public List<UserJaxb> getUsers() {
        return users;
    }

    public void setUsers(List<UserJaxb> users) {
        this.users = users;
    }
}
