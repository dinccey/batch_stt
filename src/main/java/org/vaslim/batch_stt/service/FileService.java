package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

@Service
public interface FileService {

    //receive video/audio file as input, send it to whisper ai and return the resulting text file as OutputStream
    File processFile(File file, String outputFilePathName, EndpointsApi endpointsApi) throws IOException;

    void findUnprocessedFiles(Path path);

    File extractAudio(File videoFile) throws IOException;

    void saveAsProcessed(String videoPath, String outputPath);

    void saveToProcess(String path, Set<Item> items, List<Item> allItems);
}
