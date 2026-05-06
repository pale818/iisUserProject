package hr.algebra.iisusers.xml;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

// JAXB-mapped class for XML unmarshalling — kept separate from the JPA User entity on purpose
// so that XML validation concerns don't bleed into the database model
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
