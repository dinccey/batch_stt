package org.vaslim.batch_stt.scheduledjob;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.service.WhisperClientService;

import java.util.concurrent.locks.ReentrantLock;

@Component
@EnableScheduling
public class TranscribingScheduledTask {
    private final WhisperClientService whisperClientService;

    private final ReentrantLock transcribingTaskReentrantLock;

    public TranscribingScheduledTask(WhisperClientService whisperClientService, ReentrantLock transcribingTaskReentrantLock) {
        this.whisperClientService = whisperClientService;
        this.transcribingTaskReentrantLock = transcribingTaskReentrantLock;
    }

    @Scheduled(cron = "${job.cron}")
    public void run() {
        if(transcribingTaskReentrantLock.tryLock()){
            try {
                whisperClientService.findUnprocessedFiles();
                whisperClientService.processAllFiles();
            } finally {
                transcribingTaskReentrantLock.unlock();
            }
        }

    }

}
