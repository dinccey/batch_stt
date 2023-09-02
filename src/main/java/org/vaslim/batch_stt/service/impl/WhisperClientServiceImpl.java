package org.vaslim.batch_stt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.batch_stt.service.WhisperClientService;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WhisperClientServiceImpl implements WhisperClientService {

    @Value("${FILESYSTEM_PATH}")
    private String filesystemPath;

    @Value("${OUTPUT_FORMAT}")
    private String outputFormat;

    private final FileService fileService;

    private final ItemRepository itemRepository;

    public WhisperClientServiceImpl(FileService fileService, ItemRepository itemRepository) {
        this.fileService = fileService;
        this.itemRepository = itemRepository;
    }

    @Override
    public void processAllFiles() {
        List<Item> unprocessedItems = itemRepository.findAllByFilePathTextIsNull();
        unprocessedItems.forEach(item -> {
            String videoPath = item.getFilePathVideo();
            File videoFile = new File(videoPath);
            try {
                String outputFileName = videoFile.getAbsolutePath().substring(0,videoFile.getAbsolutePath().lastIndexOf(".")) + "." + outputFormat;
                File audioFile = fileService.extractAudio(videoFile);
                fileService.processFile(audioFile, outputFileName);
                saveAsProcessed(videoPath , outputFileName);
            } catch (IOException e) {
                e.printStackTrace();
                //throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void findUnprocessedFiles() {
        File directory = new File(filesystemPath);
        String[] directories = directory.list((current, name) -> new File(current, name).isDirectory());
        if (directories != null) {
            for (String dir : directories) {
                System.out.println(directory+ "/"+ dir);
                fileService.findUnprocessedFiles(Path.of(directory+ "/"+ dir));
            }
        }
    }

    private void saveAsProcessed(String videoPath, String outputPath){
        Optional<Item> item = itemRepository.findByFilePathVideoEquals(videoPath);
        if(item.isPresent()){
            item.get().setFilePathText(outputPath);
            item.get().setProcessedTimestamp(LocalDateTime.now());
            itemRepository.save(item.get());
        }
    }
}