package org.vaslim.batch_stt.service.impl;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaslim.batch_stt.constants.Constants;
import org.vaslim.batch_stt.enums.ProcessingStatus;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private static final String TASK_TRANSCRIBE = "transcribe";

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final ItemRepository itemRepository;

    private int counter = 0;

    @Value("${output.format}")
    private String outputFormat;

    @Value("${mp3.save}")
    private boolean saveAudio;

    @Value("${excluded.paths}")
    private String[] excludedPaths;

    public FileServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
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

    @Transactional
    @Override
    public void findUnprocessedFiles(Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            deleteExcludedItemsFromDb(excludedPaths);
            List<Path> fileList = paths.filter(Files::isRegularFile).toList();
            Set<String> filePaths = new HashSet<>();
            for (Path filePath : fileList) {
                if(Arrays.stream(excludedPaths).noneMatch(filePath::startsWith)){
                    filePaths.add(filePath.toString());
                }
            }
            logger.info("Number of filePaths: " + filePaths.size());
            //check that filePath doesn't end with text file extension or that it is a backup of the word filter
            List<String> videoPaths = filePaths.stream().filter(filePath ->  Constants.Files.IGNORE_EXTENSIONS.stream().noneMatch(filePath::endsWith)
                    && Constants.Files.TRANSCRIBE_EXTENSIONS.stream().noneMatch(filePath::endsWith)
                    && !filePath.contains(outputFormat + "+")).toList();
            Set<Item> itemsToSave = new HashSet<>();
            videoPaths.forEach(videoPath->saveToProcess(videoPath, itemsToSave));
            //List<String> textPaths = filePaths.stream().filter(filePath -> Constants.Files.TRANSCRIBE_EXTENSIONS.stream().anyMatch(filePath::endsWith)).toList();
            //logger.info("Found text paths from transcribe extensions: " + textPaths.size());
            //saveProcessed(textPaths);
            itemRepository.saveAllAndFlush(itemsToSave);
        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
            throw new BatchSttException(e.getMessage());
        }
    }

    protected void saveProcessed(List<String> textPaths) {
        AtomicInteger counterTxt = new AtomicInteger();
        textPaths.forEach(textPath->{
            System.out.println("I am here 1");
            String subtitleName = textPath.substring(0,textPath.lastIndexOf("."));
            System.out.println("I am here 2 " + textPath + " ; " + subtitleName);
            saveAsProcessed(subtitleName, textPath);
            System.out.println("I am here 3");
            counterTxt.getAndIncrement();
        });
    }

    @Override
    public File extractAudio(File videoFile) throws IOException {
        String audioFileName = "tmp.mp3";
        if(saveAudio){
            audioFileName = videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf(".")) + ".mp3";
        }
        File audioFile = new File(audioFileName);
        if(audioFile.exists()){
            return audioFile;
        }
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFile)) {
            grabber.setOption("-vn","");
            grabber.start();
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(audioFile, 1)) {
                recorder.setAudioCodec(avcodec.AV_CODEC_ID_MP3);
                recorder.setAudioQuality(0);
                recorder.setAudioBitrate(128000);
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setImageWidth(0);
                recorder.setImageHeight(0);
                recorder.start();
                while (true) {
                    try {
                        recorder.record(grabber.grabSamples());
                    } catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
                        break;
                    }
                }
            } catch (FFmpegFrameRecorder.Exception e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Audio extracted successfully!");
        return audioFile;
    }

    @Override
    public void saveToProcess(String path, Set<Item> items){

        System.out.println("I am here 1");
        String noExtensionPath = path.substring(0,path.lastIndexOf("."));
        try {
            System.out.println("I am here 2");
            if(Constants.Files.TRANSCRIBE_EXTENSIONS.stream().anyMatch(path::contains)
                    || Constants.Files.IGNORE_EXTENSIONS.stream().anyMatch(path::contains)) return;
            System.out.println("I am here 3" + noExtensionPath);
            Item item = itemRepository.findByFilePathVideoStartingWith(noExtensionPath).orElse(null);
            System.out.println("I am here 4");
            if(item == null) item = new Item();
            String processedFilePAth = findProcessedFilePath(noExtensionPath);
            System.out.println("I am here 5");
            if(processedFilePAth != null){
                item.setFilePathText(processedFilePAth);
                item.setProcessingStatus(ProcessingStatus.FINISHED);
            }
            System.out.println("I am here 6");
            item.setFilePathVideo(path);
            items.add(item);
        } catch (Exception e){
            throw new BatchSttException(e.getMessage());
        }
    }

    private String findProcessedFilePath(String path) {
        AtomicReference<String> existingFile = new AtomicReference<>();
        Constants.Files.TRANSCRIBE_EXTENSIONS.forEach(transcribeExtension->{
            String filePath = path+transcribeExtension;
            if(new File(filePath).exists()){
                existingFile.set(filePath);
            }
        });
        return existingFile.get();
    }

    @Override
    public void saveAsProcessed(String pathNoExtension, String outputPath){
        System.out.println("I am here 2+");
        Optional<Item> item = itemRepository.findByFilePathVideoStartingWith(pathNoExtension.trim());
        System.out.println("I am here 4");
        if(item.isPresent() && outputPath != null){
            try{
                System.out.println("I am here 5");
                item.get().setFilePathText(outputPath);
                item.get().setProcessedTimestamp(LocalDateTime.now());
                item.get().setProcessingStatus(ProcessingStatus.FINISHED);
                System.out.println("I am here 6");
                item.get().setVideoFileName(pathNoExtension.substring(pathNoExtension.lastIndexOf("/")+1));
                counter++;
                System.out.println("I am here 7");
                itemRepository.saveAndFlush(item.get());
                System.out.println("I am here 8");
            } catch (Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
        else{
            logger.error("Video path to save as processed not found: " + pathNoExtension + " outputPath = " + outputPath);
        }
    }

    public void deleteExcludedItemsFromDb(String[] excludedPaths){
        for (String path: excludedPaths){
            logger.info("Excluded path: " + path);
            itemRepository.deleteItemByFilePathVideoStartingWith(path);
        }
    }

}
