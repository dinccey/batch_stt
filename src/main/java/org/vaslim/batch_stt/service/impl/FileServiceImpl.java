package org.vaslim.batch_stt.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.constants.Constants;
import org.vaslim.batch_stt.enums.ProcessingStatus;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileScanService;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class FileServiceImpl implements FileService {

    private static final String TASK_TRANSCRIBE = "transcribe";

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final ItemRepository itemRepository;

    private final FileScanService fileScanService;

    @Value("${output.format}")
    private String outputFormat;

    @Value("${excluded.paths}")
    private String[] excludedPaths;

    public FileServiceImpl(ItemRepository itemRepository, FileScanService fileScanService) {
        this.itemRepository = itemRepository;
        this.fileScanService = fileScanService;
    }

    @Override
    public File processFile(File file, String outputFilePathName, EndpointsApi endpointsApi) throws IOException {
        logger.info("Starting inference on " + endpointsApi.getApiClient().getBasePath() + " " + file.getName());
        byte[] fileContent = endpointsApi.asrAsrPost(file, TASK_TRANSCRIBE,"","", true, outputFormat);
        FileOutputStream fos = new FileOutputStream(outputFilePathName);
        fos.write(fileContent);
        fos.close();

        return file;
    }

    @Override
    public void findUnprocessedFiles() {
        deleteExcludedItemsFromDb(excludedPaths);
        scanFiles();
        fileScanService.reset();
        scanProcessedFiles();
        fileScanService.reset();
        logger.info("Total items count with extension .mp4 " + itemRepository.countItemsByFilePathVideoEndingWith(".mp4"));
        Set<Item> items = itemRepository.findItemsByFilePathVideoNotContaining(".mp4");
        items.forEach(item -> {
            logger.info("Extra item?: " + item.getFilePathVideo() + " with ID: " + item.getId());
        });
    }

    private void scanFiles() {
        List<File> nextFiles;
        do{
            nextFiles = fileScanService.getNext();
            nextFiles.forEach(file -> {
                if(file.isFile() && Constants.Files.IGNORE_EXTENSIONS.stream().noneMatch(file.getName()::endsWith)
                        && Arrays.stream(excludedPaths).noneMatch(file.getAbsolutePath()::startsWith)){
                    try {
                        if (Constants.Files.TRANSCRIBE_EXTENSIONS.stream().noneMatch(file.getName()::endsWith)){
                            saveToProcess(file.getAbsolutePath());
                        }
                    } catch (Exception e){
                        //logger.error(e.getMessage());
                    }
                }

            });
            itemRepository.flush();

        } while (!nextFiles.isEmpty());
    }

    private void scanProcessedFiles() {
        List<File> nextFiles;
        do{
            nextFiles = fileScanService.getNext();
            nextFiles.forEach(file -> {
                if(file.isFile() && Constants.Files.IGNORE_EXTENSIONS.stream().noneMatch(file.getName()::endsWith)){
                    try {
                        if (Constants.Files.TRANSCRIBE_EXTENSIONS.stream().anyMatch(file.getName()::endsWith)){
                            saveAsProcessed(file.getAbsolutePath());
                        }
                    } catch (Exception e){
                        //logger.error(e.getMessage());
                    }
                }

            });
            itemRepository.flush();

        } while (!nextFiles.isEmpty());
    }

    @Override
    public void saveAsProcessed(String path) {
        try {
            String noExtensionPath = path.substring(0,path.lastIndexOf("."));
            Item item = itemRepository.findByFilePathVideoStartingWith(noExtensionPath+".").orElse(null);
            if(item == null) return;
            item.setFilePathText(path);
            item.setProcessingStatus(ProcessingStatus.FINISHED);
            itemRepository.save(item);
        } catch (Exception e){
            throw new BatchSttException(e.getMessage());
        }
    }

    @Override
    public void saveToProcess(String path){

        try {
            Item item = new Item();
            item.setFilePathVideo(path);
            itemRepository.save(item);
        } catch (Exception e){
            throw new BatchSttException(e.getMessage());
        }
    }

    @Override
    public File extractAudio(File videoFile) {
        String audioFileName = videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf(".")) + ".mp3";
        File audioFile = new File(audioFileName);

        if(audioFile.exists()){
            return audioFile;
        }

        return null;
    }

    public void deleteExcludedItemsFromDb(String[] excludedPaths){
        for (String path: excludedPaths){
            logger.info("Excluded path: " + path);
            itemRepository.deleteItemByFilePathVideoStartingWith(path);
        }
    }

}
