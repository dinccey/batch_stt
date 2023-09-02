package org.vaslim.batch_stt.scheduledjob;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.service.WhisperClientService;

@Component
@EnableScheduling
public class ScheduledTask {
    private WhisperClientService whisperClientService;

    public ScheduledTask(WhisperClientService whisperClientService) {
        this.whisperClientService = whisperClientService;
    }

    @Scheduled(cron = "${JOB_CRON}")
    public void run() {
        whisperClientService.findUnprocessedFiles();
        whisperClientService.processAllFiles();
    }

}
