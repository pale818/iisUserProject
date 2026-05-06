package hr.algebra.paola.auth;

public record  AuthResponse(String accessToken, String refreshToken, String role) {
}

