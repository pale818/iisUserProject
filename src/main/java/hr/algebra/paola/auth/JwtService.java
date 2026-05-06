package hr.algebra.paola.auth;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    //sets secret( decode it ) and expiry values
    //builds tokens
    //claims: decodes/parses the parts of a token
    //validate expiry- using claims
    //find username from token-claims


    @Value("")
    private String secret;

    @Value("${jwt.access-token-expiry-ms}")
    private long accessExpiry;

    @Value("${jwt.refresh-token-expiry-ms}")
    private long refreshExpiry;

    public String generateAccessToken(String username) { return buildToken(username,accessExpiry);}

    public String generateRefreshToken(String username) { return buildToken(username,refreshExpiry); }

    public String buildToken(String username, long expiry) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiry))
                .signWith(getSecret())
                .compact();
    }


    private SecretKey getSecret() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean validateToken(String token) {
        try{
            getClaims(token);
            return true;
        }catch (JwtException e){
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecret())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
