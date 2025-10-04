package ai.smartcity.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(toBase64(secret)));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(UserDetails user){
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsername(String token){
        return parseAllClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails user){
        String u = getUsername(token);
        return u != null && u.equals(user.getUsername()) && !isExpired(token);
    }

    private boolean isExpired(String token){
        return parseAllClaims(token).getExpiration().before(new Date());
    }

    private Claims parseAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private static String toBase64(String s){
        return java.util.Base64.getEncoder()
                .encodeToString(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }
}
