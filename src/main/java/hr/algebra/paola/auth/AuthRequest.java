package hr.algebra.paola.auth;

import org.springframework.security.core.Authentication;

public record AuthRequest (String username, String password){
}
