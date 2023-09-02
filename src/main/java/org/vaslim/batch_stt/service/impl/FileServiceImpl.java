package org.vaslim.batch_stt.service.impl;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.ItemDTO;
import org.vaslim.batch_stt.model.Item;
import org.vaslim.batch_stt.repository.ItemRepository;
import org.vaslim.batch_stt.service.FileService;
import org.vaslim.whisper_asr.client.api.EndpointsApi;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileServiceImpl implements FileService {

    private static final String TASK_TRANSCRIBE = "transcribe";
    private final EndpointsApi endpointsApi;

    private final ItemRepository itemRepository;

    public FileServiceImpl(EndpointsApi endpointsApi, ItemRepository itemRepository) {
        this.endpointsApi = endpointsApi;
        this.itemRepository = itemRepository;
    }

    @Override
    public File processFile(File file) throws IOException {

        byte[] fileContent = endpointsApi.asrAsrPost(file, TASK_TRANSCRIBE,"","", true, "srt");
        String subtitleName = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf(".")) + ".srt";
        FileOutputStream fos = new FileOutputStream(subtitleName);
        fos.write(fileContent);
        fos.close();

        return file;
    }

    @Override
    public void findUnprocessedFiles(Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            List<Path> fileList = paths.filter(Files::isRegularFile).toList();
            Set<String> filePaths = new HashSet<>();
            Set<String> fileNames = new HashSet<>();
            for (Path filePath : fileList) {
                String fileName = filePath.getFileName().toString();
                String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
                if (fileNames.contains(nameWithoutExtension)) {
                    System.out.println("Two files with the same name but different extension exist: " + fileName + " and " + fileNames.stream().filter(nameWithoutExtension::equals).findFirst().get());
                } else {
                    fileNames.add(nameWithoutExtension);
                    filePaths.add(filePath.toString());
                }
            }
            filePaths.stream().filter(filePath -> !filePath.endsWith("srt")).forEach(this::saveToProcess);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public File extractAudio(File videoFile) throws IOException {

        File audioFile = new File(videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf('.')) + ".mp3");

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

    private void saveToProcess(String path){
        if(itemRepository.existsItemByFilePathVideoEquals(path)) return;
        Item item = new Item();
        item.setFilePathVideo(path);
        itemRepository.save(item);
    }

    private static File convert(InputStream inputStream) throws IOException {
        File tempFile = Files.createTempFile("temp", ".tmp").toFile();
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

}
