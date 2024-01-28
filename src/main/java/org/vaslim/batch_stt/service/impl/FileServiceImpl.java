package org.vaslim.batch_stt.service.impl;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
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
import java.util.List;

@Service
public class FileServiceImpl implements FileService {

    private static final String TASK_TRANSCRIBE = "transcribe";

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    private final ItemRepository itemRepository;

    private final FileScanService fileScanService;

    @Value("${output.format}")
    private String outputFormat;

    @Value("${mp3.save}")
    private boolean saveAudio;

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
    }

    private void scanFiles() {
        List<File> nextFiles;
        do{
            nextFiles = fileScanService.getNext();
            nextFiles.forEach(file -> {
                if(file.isFile() && Constants.Files.IGNORE_EXTENSIONS.stream().noneMatch(file.getName()::endsWith)){
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

    public void deleteExcludedItemsFromDb(String[] excludedPaths){
        for (String path: excludedPaths){
            logger.info("Excluded path: " + path);
            itemRepository.deleteItemByFilePathVideoStartingWith(path);
        }
    }

}
