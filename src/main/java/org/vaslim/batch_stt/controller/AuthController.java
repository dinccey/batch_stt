package org.vaslim.batch_stt.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.vaslim.batch_stt.dto.AppUserDTO;
import org.vaslim.batch_stt.dto.AuthenticationRequestDTO;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.repository.AppUserRepository;
import org.vaslim.batch_stt.service.AuthService;
import org.vaslim.batch_stt.service.impl.UserDetailsImpl;
import org.vaslim.batch_stt.util.JwtUtils;

import java.util.Optional;


//@CrossOrigin(origins = "${frontend.origin}", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController
{

    private final AuthService authService;

    private final AppUserRepository appUserRepository;
    private final ModelMapper modelMapper;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, AppUserRepository appUserRepository, ModelMapper modelMapper, JwtUtils jwtUtils)
    {
        this.authService = authService;
        this.appUserRepository = appUserRepository;
        this.modelMapper = modelMapper;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<AppUserDTO> authenticateUser(@Valid @RequestBody AuthenticationRequestDTO authenticationRequestDTO) {

        UserDetails userDetails = authService.authenticate(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword());
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie((UserDetailsImpl) userDetails);

        Optional<AppUser> appUserDTO = appUserRepository.findByUsername(userDetails.getUsername());
        appUserDTO.get().setPassword(null);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(modelMapper.map(appUserDTO.get(), AppUserDTO.class));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("You've been signed out!");
    }
}
