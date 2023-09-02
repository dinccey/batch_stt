package org.vaslim.batch_stt.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.batch_stt.service.WhisperClientService;

import java.io.*;
import java.nio.file.Path;
import java.util.List;

@Service
public class WhisperClientServiceImpl implements WhisperClientService {

    @Value("${FILESYSTEM_PATH}")
    private String filesystemPath;

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
                File audioFile = fileService.extractAudio(videoFile);
                File srtFile = fileService.processFile(audioFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                FileOutputStream fos = new FileOutputStream(srtFile);
                byteArrayOutputStream.writeTo(fos);
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
}
