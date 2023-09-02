package org.vaslim.batch_stt.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.vaslim.batch_stt.dto.AuthenticationRequestDTO;
import org.vaslim.batch_stt.service.AuthService;
import org.vaslim.batch_stt.service.impl.UserDetailsImpl;
import org.vaslim.batch_stt.util.JwtUtils;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/auth")
public class AuthController
{

    private final AuthService authService;

    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils)
    {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {

        UserDetails userDetails = authService.authenticate(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword());
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie((UserDetailsImpl) userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).build();
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }
}
