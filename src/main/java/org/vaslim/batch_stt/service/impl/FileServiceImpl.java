package org.vaslim.batch_stt.service.impl;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.constants.Constants;
import org.vaslim.batch_stt.enums.ProcessingStatus;
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
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private static final String TASK_TRANSCRIBE = "transcribe";

    private final ItemRepository itemRepository;

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

        byte[] fileContent = endpointsApi.asrAsrPost(file, TASK_TRANSCRIBE,"","", true, outputFormat);
        FileOutputStream fos = new FileOutputStream(outputFilePathName);
        fos.write(fileContent);
        fos.close();

        return file;
    }

    @Override
    public void findUnprocessedFiles(Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            List<Path> fileList = paths.filter(Files::isRegularFile).toList();
            Set<String> filePaths = new HashSet<>();
            for (Path filePath : fileList) {
                if(Arrays.stream(excludedPaths).noneMatch(filePath::startsWith)){
                    filePaths.add(filePath.toString());
                }
            }
            List<String> videoPaths = filePaths.stream().filter(filePath -> !filePath.endsWith(outputFormat)).toList();
            videoPaths.forEach(this::saveToProcess);
            List<String> textPaths = filePaths.stream().filter(filePath -> filePath.endsWith(outputFormat)).toList();
            textPaths.forEach(textPath->{
                if(Constants.Files.transcribeExtensions.stream().anyMatch(textPath::contains)){
                    String subtitleName = textPath.substring(0,textPath.lastIndexOf("."));
                    String videoPath = videoPaths.stream().filter(video->video.substring(0,video.lastIndexOf(".")).equals(subtitleName)
                            && Constants.Files.ignoreExtensions.stream().noneMatch(video::endsWith)).findFirst().get();
                    saveAsProcessed(videoPath, textPath);
                }
            });

        } catch (IOException | NoSuchElementException e) {
            e.printStackTrace();
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
                recorder.setAudioBitrate(192000);
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
    public void saveToProcess(String path){
        if(itemRepository.existsItemByFilePathVideoLike(path)
                || Constants.Files.transcribeExtensions.stream().anyMatch(path::contains)
                || Constants.Files.ignoreExtensions.stream().anyMatch(path::contains)) return;
        Item item = new Item();
        item.setFilePathVideo(path);
        itemRepository.save(item);
    }

    @Override
    public void saveAsProcessed(String videoPath, String outputPath){
        Optional<Item> item = itemRepository.findByFilePathVideoEquals(videoPath);
        if(item.isPresent()){
            try{
                item.get().setFilePathText(outputPath);
                item.get().setProcessedTimestamp(LocalDateTime.now());
                item.get().setProcessingStatus(ProcessingStatus.FINISHED);
                item.get().setVideoFileName(videoPath.substring(videoPath.lastIndexOf("/")+1));
                itemRepository.save(item.get());
            } catch (Exception e){

            }
        }
    }

}
