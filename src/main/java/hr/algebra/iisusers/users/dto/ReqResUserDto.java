package hr.algebra.iisusers.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO for deserializing a single user from the ReqRes API response
public class ReqResUserDto {

    private Integer id;
    private String email;

    // ReqRes uses snake_case; @JsonProperty maps the JSON field to the camelCase Java field
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String avatar;

    public ReqResUserDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
