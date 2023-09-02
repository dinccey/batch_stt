package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;

@Service
public interface WhisperClientService {
    void processAllFiles();

    void findUnprocessedFiles();
}
