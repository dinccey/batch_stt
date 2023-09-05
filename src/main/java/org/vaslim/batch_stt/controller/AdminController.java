package org.vaslim.batch_stt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vaslim.batch_stt.scheduledjob.TranscribingScheduledTask;
import org.vaslim.batch_stt.service.WhisperClientService;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final TranscribingScheduledTask transcribingScheduledTask;
    private final WhisperClientService whisperClientService;

    public AdminController(TranscribingScheduledTask transcribingScheduledTask, WhisperClientService whisperClientService) {
        this.transcribingScheduledTask = transcribingScheduledTask;
        this.whisperClientService = whisperClientService;
    }

    @GetMapping("/run")
    public ResponseEntity<?> run(){
        whisperClientService.findUnprocessedFiles();
        whisperClientService.processAllFiles();
        return ResponseEntity.ok().build();
    }

}
