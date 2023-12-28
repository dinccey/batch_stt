package org.vaslim.batch_stt.scheduledjob;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.vaslim.batch_stt.pool.ConnectionPool;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.service.InferenceInstanceService;

@Component
@EnableScheduling
public class InferenceInstanceOnlineCheckScheduledTask {

    private final InferenceInstanceRepository inferenceInstanceRepository;

    private final InferenceInstanceService inferenceInstanceService;

    private final ConnectionPool connectionPool;

    public InferenceInstanceOnlineCheckScheduledTask(InferenceInstanceRepository inferenceInstanceRepository, InferenceInstanceService inferenceInstanceService, ConnectionPool connectionPool) {
        this.inferenceInstanceRepository = inferenceInstanceRepository;
        this.inferenceInstanceService = inferenceInstanceService;
        this.connectionPool = connectionPool;
    }

    @Scheduled(cron = "*/1 * * * * *")
    public void run() {
        inferenceInstanceRepository.findAll().forEach(inferenceInstance -> {
            inferenceInstance.setAvailable(inferenceInstanceService.checkIsReachable(inferenceInstance.getInstanceUrl()));
            inferenceInstanceRepository.save(inferenceInstance);
        });
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void runRefreshConnectionPool() {
        connectionPool.refreshUrlsFromDatabase();
    }

}
