package org.vaslim.batch_stt.scheduledjob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.pool.ConnectionPool;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.service.InferenceInstanceService;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class InferenceInstanceOnlineCheckScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(InferenceInstanceOnlineCheckScheduledTask.class);

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
        //logger.info("online check.");
        Set<String> instanceIds = inferenceInstanceRepository.findAll().stream().map(InferenceInstance::getInstanceUrl).collect(Collectors.toSet());
        instanceIds.forEach(id->{

        });
        inferenceInstanceRepository.findAll().forEach(inferenceInstance -> {
            boolean valueBefore = inferenceInstance.getAvailable();
            boolean valueAfter = inferenceInstanceService.checkIsReachable(inferenceInstance.getInstanceUrl());
            inferenceInstance.setAvailable(valueAfter);
            inferenceInstanceRepository.save(inferenceInstance);
            if(valueAfter != valueBefore){
                logger.warn(inferenceInstance.getInstanceUrl() + "'s online status went from " + valueBefore + " to " + valueAfter);
            }
        });
    }

    @Scheduled(cron = "*/10 * * * * *")
    public void runRefreshConnectionPool() {
        logger.info("Refresh connection pool.");
        connectionPool.refreshUrlsFromDatabase();
    }

}
