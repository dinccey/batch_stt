package org.vaslim.batch_stt.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import org.vaslim.batch_stt.scheduledjob.FilteringScheduledTask;
import org.vaslim.batch_stt.scheduledjob.TranscribingScheduledTask;
import org.vaslim.batch_stt.service.WhisperClientService;
import org.vaslim.batch_stt.util.JwtUtils;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final TranscribingScheduledTask transcribingScheduledTask;
    private final WhisperClientService whisperClientService;
    private final JwtUtils jwtUtils;
    private final FilteringScheduledTask filteringScheduledTask;

    @Value("${batchstt_jwt}")
    private String cookieName;

    public AdminController(TranscribingScheduledTask transcribingScheduledTask, WhisperClientService whisperClientService, JwtUtils jwtUtils, FilteringScheduledTask filteringScheduledTask) {
        this.transcribingScheduledTask = transcribingScheduledTask;
        this.whisperClientService = whisperClientService;
        this.jwtUtils = jwtUtils;
        this.filteringScheduledTask = filteringScheduledTask;
    }

    @GetMapping("/run")
    public ResponseEntity<?> run(final HttpServletRequest httpServletRequest){
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        String username = jwtUtils.getUserNameFromJwtToken(cookie.getValue());
        if(username.equals("admin")){
            whisperClientService.findUnprocessedFiles();
            whisperClientService.processAllFiles();
            filteringScheduledTask.run();
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
