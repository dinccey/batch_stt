package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.File;
import java.io.IOException;

@Service
public interface FileService {

    //receive video/audio file as input, send it to whisper ai and return the resulting text file as OutputStream
    File processFile(File file, String outputFilePathName, EndpointsApi endpointsApi) throws IOException;

    void findUnprocessedFiles();

    File extractAudio(File videoFile) throws IOException;

    void saveAsProcessed(String path);

    void saveToProcess(String path);
}
