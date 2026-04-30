package hr.algebra.iisusers.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByRefreshToken(String refreshToken);

}