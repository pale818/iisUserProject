package hr.algebra.paola.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRepo extends JpaRepository {
    Optional<AppUser>findByUsername(String username);
    Optional<AppUser> findByRefreshToken(String email);
}
