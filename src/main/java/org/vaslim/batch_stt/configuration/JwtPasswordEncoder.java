package org.vaslim.batch_stt.configuration;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class JwtPasswordEncoder implements PasswordEncoder {
    private final Key key;

    public JwtPasswordEncoder() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] passwordBytes = rawPassword.toString().getBytes(StandardCharsets.UTF_8);
        return Jwts.builder().setSubject("user").signWith(key).compact();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(encode(rawPassword));
    }
}
