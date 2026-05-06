package hr.algebra.iisusers;

import hr.algebra.iisusers.auth.AppUser;
import hr.algebra.iisusers.auth.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class IisUsersProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(IisUsersProjectApplication.class, args);
    }

    // Runs once on startup — inserts demo users only if they don't already exist
    @Bean
    CommandLineRunner seedUsers(AppUserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("reader").isEmpty()) {
                repo.save(AppUser.builder()
                        .username("reader")
                        .password(encoder.encode("reader123"))
                        .role("READ_ONLY")   // can only read via GET
                        .build());
            }
            if (repo.findByUsername("admin").isEmpty()) {
                repo.save(AppUser.builder()
                        .username("admin")
                        .password(encoder.encode("admin123"))
                        .role("FULL_ACCESS") // can create, update, delete
                        .build());
            }
        };
    }
}