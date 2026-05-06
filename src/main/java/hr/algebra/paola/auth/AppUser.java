package hr.algebra.paola.auth;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="app_user")
public class AppUser {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 1024)
    private String refreshToken;

    @Column(nullable = false)
    private String role;


}
