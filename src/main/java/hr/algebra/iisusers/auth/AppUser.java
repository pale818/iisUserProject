package hr.algebra.iisusers.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Either "READ_ONLY" or "FULL_ACCESS"
    @Column(nullable = false)
    private String role;

    // Stored here so we can look up the user by their refresh token
    @Column(length = 1024)
    private String refreshToken;
}