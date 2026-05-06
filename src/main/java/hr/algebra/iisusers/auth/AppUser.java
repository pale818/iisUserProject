package hr.algebra.iisusers.auth;

import jakarta.persistence.*;
import lombok.*;

// Stored in the app_users table — separate from the users table which holds public user data
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
    private String password; // BCrypt-hashed, never stored in plain text

    @Column(nullable = false)
    private String role; // "READ_ONLY" or "FULL_ACCESS" — prefixed with ROLE_ by AppUserDetailsService

    // Refresh token stored in DB so it can be validated and revoked on logout
    @Column(length = 1024)
    private String refreshToken;
}