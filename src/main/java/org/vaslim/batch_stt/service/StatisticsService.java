package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.StatisticsDTO;

@Service
public interface StatisticsService {
    StatisticsDTO getUserStatistics(String username);

    void incrementProcessedItemsPerInstance(String inferenceInstanceUrl);

    void incrementTotalProcessingTimePerInstance(String inferenceInstance, long timeMillis);
}
