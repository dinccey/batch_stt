package org.vaslim.batch_stt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;
import org.vaslim.batch_stt.dto.AppUserDTO;
import org.vaslim.batch_stt.scheduledjob.FilteringScheduledTask;
import org.vaslim.batch_stt.scheduledjob.TranscribingScheduledTask;
import org.vaslim.batch_stt.service.AdminService;
import org.vaslim.batch_stt.service.WhisperClientService;
import org.vaslim.batch_stt.util.JwtUtils;

import java.util.Set;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final TranscribingScheduledTask transcribingScheduledTask;
    private final WhisperClientService whisperClientService;
    private final JwtUtils jwtUtils;
    private final FilteringScheduledTask filteringScheduledTask;

    private final AdminService adminService;

    @Value("${batchstt.jwtCookieName}")
    private String cookieName;

    @Value("${spring.security.user.name}")
    private String adminUsername;

    public AdminController(TranscribingScheduledTask transcribingScheduledTask, WhisperClientService whisperClientService, JwtUtils jwtUtils, FilteringScheduledTask filteringScheduledTask, AdminService adminService) {
        this.transcribingScheduledTask = transcribingScheduledTask;
        this.whisperClientService = whisperClientService;
        this.jwtUtils = jwtUtils;
        this.filteringScheduledTask = filteringScheduledTask;
        this.adminService = adminService;
    }

    @GetMapping("/run")
    public ResponseEntity<?> run(final HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());
        if(username.equals(adminUsername)){
            whisperClientService.findUnprocessedFiles();
            whisperClientService.processAllFiles();
            filteringScheduledTask.run();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/user/add")
    public ResponseEntity<AppUserDTO> addUser(@Valid @RequestBody AppUserDTO appUserDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        if(username.equals(adminUsername)){

            return ResponseEntity.ok(adminService.addUser(appUserDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/user/edit")
    public ResponseEntity<AppUserDTO> editUser(@Valid @RequestBody AppUserDTO appUserDTO, final HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        if(username.equals(adminUsername)){

            return ResponseEntity.ok(adminService.editUser(appUserDTO));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/user/remove/{username}")
    public ResponseEntity<?> removeUser(@PathVariable("username") String user, final HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        if(username.equals(adminUsername)){
            adminService.removeUser(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("user/all")
    public ResponseEntity<Set<AppUserDTO>> allUsers(final HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        assert cookie != null;
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());

        if(username.equals(adminUsername)){
            return ResponseEntity.ok(adminService.getAllUsers());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
