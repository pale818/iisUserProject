package hr.algebra.iisusers.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

// JAXB-annotated class that mirrors the <user> element in user.xsd.
// Used by XmlValidationService to unmarshal and validate a single user XML body.
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserJaxb {

    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private String avatar;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
}