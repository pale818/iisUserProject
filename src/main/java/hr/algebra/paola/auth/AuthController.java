package hr.algebra.paola.auth;


import hr.algebra.paola.users.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    //login function
    //refreshToken function

    private final AuthenticationManager authManager;
    private final  JwtService jwtService;
    private final AppRepo repository;

    public AuthController(AuthenticationManager authManager,JwtService jwtService,AppRepo repository) {
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.repository = repository;
    }


    @PostMapping("/login")
    public ResponseEntity<?>login(AuthRequest authRequest ) {
        try{
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
            );
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AppUser user = repository.findByUsername(authRequest.username()).orElseThrow();
        String accessToken= jwtService.generateAccessToken(user.getUsername());
        String refreshToken= jwtService.generateRefreshToken(user.getUsername());
        user.setRefreshToken(refreshToken);
        repository.save(user);

        return ResponseEntity.ok().body(new AuthResponse(accessToken, refreshToken, user.getRole()));


    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) throws  Exception{
        String token = refreshToken.trim();
        return repository.findByRefreshToken(token)
                .filter(u ->jwtService.validateToken(token))
                .map(u-> ResponseEntity.ok(jwtService.generateAccessToken(u.getUsername())))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token"));
    }

}
