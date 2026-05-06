package hr.algebra.iisusers.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final AppUserRepository userRepo;

    public AuthController(AuthenticationManager authManager,
                          JwtService jwtService,
                          AppUserRepository userRepo) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            //  credential check to Spring Security's AuthenticationManager
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        AppUser user = userRepo.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        // Persist the refresh token so it can be validated and revoked later
        user.setRefreshToken(refreshToken);
        userRepo.save(user);

        // Returns both tokens and the role so the frontend can adjust the UI immediately
        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, user.getRole()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        String token = refreshToken.trim();
        // Valid only if the token exists in the DB (not revoked) AND is not expired
        return userRepo.findByRefreshToken(token)
                .filter(u -> jwtService.isTokenValid(token))
                .map(u -> ResponseEntity.ok(jwtService.generateAccessToken(u.getUsername())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token"));
    }
}
