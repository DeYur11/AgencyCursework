package org.example.advertisingagency.service.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.advertisingagency.model.Worker;
import org.example.advertisingagency.model.WorkerAccount;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenService {

    private static final long EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 години
    private static final String ISSUER = "AdvertisingAgency";
    private Key key;

    @PostConstruct
    public void init() {
        // Можна згенерувати через UUID і зберегти як env var
        String secret = "a-very-secret-key-that-should-be-long-enough-and-stored-securely";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private static final Map<String, String> positionToRoleMap = Map.of(
            "Project Manager", "PROJECT_MANAGER",
            "Designer", "WORKER",
            "Marketer", "WORKER",
            "Copywriter", "WORKER",
            "Analyst", "WORKER",
            "Scrum Master", "SCRUM_MASTER"
    );

    public String generateToken(WorkerAccount account) {
        Worker worker = account.getWorker();
        String positionName = worker.getPosition().getName();

        return Jwts.builder()
                .setIssuer(ISSUER)
                .setSubject(worker.getId().toString())
                .claim("username", account.getUsername())
                .claim("name", worker.getName())
                .claim("surname", worker.getSurname())
                .claim("role", positionToRoleMap.get(positionName))
                .claim("isReviewer", worker.getIsReviewer())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public Integer extractWorkerId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Integer.parseInt(claims.getSubject());
    }

    public String extractUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    // ✅ Додано метод для витягування ролі
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}
