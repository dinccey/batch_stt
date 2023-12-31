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

    private final ReentrantLock fileRefreshTaskReentrantLock;

    private final ReentrantLock fileProcessingTaskReentrantLock;

    public TranscribingScheduledTask(WhisperClientService whisperClientService, ReentrantLock fileRefreshTaskReentrantLock, ReentrantLock fileProcessingTaskReentrantLock) {
        this.whisperClientService = whisperClientService;
        this.fileRefreshTaskReentrantLock = fileRefreshTaskReentrantLock;
        this.fileProcessingTaskReentrantLock = fileProcessingTaskReentrantLock;
    }

    @Scheduled(cron = "${job.cron}")
    public void runRefreshFiles() {
        if(fileRefreshTaskReentrantLock.tryLock()){
            try {
                whisperClientService.findUnprocessedFiles();
            } finally {
                fileRefreshTaskReentrantLock.unlock();
            }
        }

    }

    @Scheduled(cron = "*/10 * * * * *")
    public void runProcessing(){
        if(fileProcessingTaskReentrantLock.tryLock()){
            try {
                whisperClientService.processAllFiles();
            } finally {
                fileProcessingTaskReentrantLock.unlock();
            }
        }

    }

}
