package org.vaslim.batch_stt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vaslim.batch_stt.scheduledjob.ScheduledTask;
import org.vaslim.batch_stt.service.WhisperClientService;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final ScheduledTask scheduledTask;
    private final WhisperClientService whisperClientService;

    public AdminController(ScheduledTask scheduledTask, WhisperClientService whisperClientService) {
        this.scheduledTask = scheduledTask;
        this.whisperClientService = whisperClientService;
    }

    @GetMapping("/run")
    public ResponseEntity<?> run(){
        whisperClientService.findUnprocessedFiles();
        whisperClientService.processAllFiles();
        return ResponseEntity.ok().build();
    }

}
