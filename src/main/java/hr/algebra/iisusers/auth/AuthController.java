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

    // Login: returns a short-lived access token and a long-lived refresh token.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        AppUser user = userRepo.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        user.setRefreshToken(refreshToken);
        userRepo.save(user);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, user.getRole()));
    }

    // Refresh: send the refresh token in the body, get a new access token back.
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        String token = refreshToken.trim();
        return userRepo.findByRefreshToken(token)
                .filter(u -> jwtService.isTokenValid(token))
                .map(u -> ResponseEntity.ok(jwtService.generateAccessToken(u.getUsername())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token"));
    }
}