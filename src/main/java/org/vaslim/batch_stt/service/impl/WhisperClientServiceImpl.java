package org.vaslim.batch_stt.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.enums.ProcessingStatus;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.InferenceInstance;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.pool.ConnectionPool;
import org.vaslim.batch_stt.repository.InferenceInstanceRepository;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.batch_stt.service.StatisticsService;
import org.vaslim.batch_stt.service.WhisperClientService;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

@Service
public class WhisperClientServiceImpl implements WhisperClientService {

    @Value("${filesystem.path}")
    private String filesystemPath;

    @Value("${output.format}")
    private String outputFormat;

    private static final Logger logger = LoggerFactory.getLogger(WhisperClientServiceImpl.class);

    private final FileService fileService;

    private final ItemRepository itemRepository;

    private final ConnectionPool connectionPool;

    private final InferenceInstanceRepository inferenceInstanceRepository;

    private final StatisticsService statisticsService;

    public WhisperClientServiceImpl(FileService fileService, ItemRepository itemRepository, ConnectionPool connectionPool, InferenceInstanceRepository inferenceInstanceRepository, StatisticsService statisticsService) {
        this.fileService = fileService;
        this.itemRepository = itemRepository;
        this.connectionPool = connectionPool;
        this.inferenceInstanceRepository = inferenceInstanceRepository;
        this.statisticsService = statisticsService;
    }

    @Override
    public void processAllFiles() {
        List<Item> unprocessedItems = itemRepository.findAllByFilePathTextIsNull();
        //logger.info("STARTED processing loop, unprocessedItems size: " + unprocessedItems.size());
        unprocessedItems.forEach(item -> {
            String videoPath = item.getFilePathVideo();
            File videoFile = new File(videoPath);
            updateItemStatus(videoFile, ProcessingStatus.IN_PROGRESS);
            final EndpointsApi[] endpointsApi = new EndpointsApi[1];
            while (endpointsApi[0] == null){
                endpointsApi[0] = connectionPool.getConnection();
                sleepMilis(500L);
            }
            new Thread(() -> {
                try {
                    String outputFileNamePath = videoFile.getAbsolutePath().substring(0,videoFile.getAbsolutePath().lastIndexOf(".")) + "." + outputFormat;
                    File audioFile = fileService.extractAudio(videoFile);
                    long startTime = System.currentTimeMillis();
                    long endTime;
                    try {
                        if(itemRepository.findById(item.getId()).get().getFilePathText() != null) throw new BatchSttException("Item " + item.getFilePathVideo() + " already processed");

                        fileService.processFile(audioFile, outputFileNamePath, endpointsApi[0]);
                        endTime = System.currentTimeMillis();
                        if (new File(outputFileNamePath).exists()){
                            fileService.saveAsProcessed(outputFileNamePath);
                            statisticsService.incrementProcessedItemsPerInstance(endpointsApi[0].getApiClient().getBasePath());
                            statisticsService.incrementTotalProcessingTimePerInstance(endpointsApi[0].getApiClient().getBasePath(),endTime - startTime);
                        }
                        connectionPool.addConnection(endpointsApi[0].getApiClient().getBasePath());
                    } catch (Exception e) {
                        logger.warn("Inference failed with exception: " + e.getMessage() + " for " + videoPath);
                        updateItemStatus(videoFile, ProcessingStatus.PENDING);
                        updateInferenceInstanceOnFailure(endpointsApi);
                        connectionPool.addConnection(endpointsApi[0].getApiClient().getBasePath());

                    }
                } catch (Exception e) {
                    logger.warn("Inference failed with exception: " + e.getMessage() + " for " + videoPath);
                    updateItemStatus(videoFile, ProcessingStatus.PENDING);
                    updateInferenceInstanceOnFailure(endpointsApi);
                    connectionPool.addConnection(endpointsApi[0].getApiClient().getBasePath());
                }
            }).start();

        });
    }

    private void updateInferenceInstanceOnFailure(EndpointsApi[] endpointsApi) {
        InferenceInstance inferenceInstance = inferenceInstanceRepository.findByInstanceUrl(endpointsApi[0].getApiClient().getBasePath()).orElse(null);
        assert inferenceInstance != null;
        //inferenceInstance.setAvailable(false);
        inferenceInstance.setFailedRunsCount(inferenceInstance.getFailedRunsCount() + 1);
        inferenceInstanceRepository.save(inferenceInstance);
    }

    private void updateItemStatus(File videoFile, ProcessingStatus processingStatus) {
        try{
            String fileVideoPathNoExtension = videoFile.getAbsolutePath().substring(0,videoFile.getAbsolutePath().lastIndexOf("."));
            Optional<Item> item = itemRepository.findByFilePathVideoStartingWith(fileVideoPathNoExtension+".");
            if(item.isPresent()){
                item.get().setProcessingStatus(processingStatus);
                itemRepository.saveAndFlush(item.get());
            }
        } catch (Exception e){
            logger.error(e.getMessage() + " for file: " + videoFile.getAbsolutePath());
        }
    }


    private void sleepMilis(Long milis){
        try {
            sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
