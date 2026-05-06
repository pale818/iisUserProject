package hr.algebra.iisusers.auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository repo;

    public AppUserDetailsService(AppUserRepository repo) {
        this.repo = repo;
    }

    // Called by JwtFilter on every request to re-load the user's current role from the DB
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new User(
                appUser.getUsername(),
                appUser.getPassword(),
                // Spring Security expects roles prefixed with "ROLE_", so "FULL_ACCESS" → "ROLE_FULL_ACCESS"
                List.of(new SimpleGrantedAuthority("ROLE_" + appUser.getRole()))
        );
    }
}
