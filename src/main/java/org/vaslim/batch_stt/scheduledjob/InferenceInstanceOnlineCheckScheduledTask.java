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
import java.util.concurrent.*;
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
        ExecutorService executor = Executors.newFixedThreadPool(instanceIds.size()); // create a thread pool

        instanceIds.forEach(id -> {
            Future<Boolean> future = executor.submit(() -> inferenceInstanceService.checkIsReachable(id)); // submit the task to be executed

            try {
                Boolean available = future.get(5000, TimeUnit.MILLISECONDS); // get the result of the future with a timeout
                InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(id).orElse(null);

                assert inferenceInstance != null;
                Boolean availableBefore = inferenceInstance.getAvailable();
                if (availableBefore == null) availableBefore = false;
                if (availableBefore != available) {
                    inferenceInstance.setAvailable(available);
                    inferenceInstanceRepository.save(inferenceInstance);
                    logger.warn(inferenceInstance.getInstanceUrl() + "'s online status went from " + availableBefore + " to " + available);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(id).orElse(null);
                assert inferenceInstance != null;
                inferenceInstance.setAvailable(false);
                inferenceInstanceRepository.save(inferenceInstance);
                //logger.error("Error checking reachability for " + id, e);
            }
        });

        executor.shutdown(); // shut down the executor service
    }


    @Scheduled(cron = "*/10 * * * * *")
    public void runRefreshConnectionPool() {
        //logger.info("Refresh connection pool.");
        connectionPool.refreshUrlsFromDatabase();
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void cleanupZombieInstances(){
        //if connection is lost during inference, an instance will remain stuck as being used while it is available
        Set<String> instanceIds = inferenceInstanceRepository.findAll().stream().map(InferenceInstance::getInstanceUrl).collect(Collectors.toSet());
        ExecutorService executor = Executors.newFixedThreadPool(instanceIds.size()); // create a thread pool

        instanceIds.forEach(id -> {
            Future<Boolean> future = executor.submit(() -> inferenceInstanceService.checkIsWhisperAvailable(id)); // submit the task to be executed

            try {
                Boolean available = future.get(2000, TimeUnit.MILLISECONDS); // get the result of the future with a timeout
                if(available) connectionPool.addConnection(id);
            } catch (InterruptedException | ExecutionException | TimeoutException ignored) {

            }
        });

        executor.shutdown();
    }

}
