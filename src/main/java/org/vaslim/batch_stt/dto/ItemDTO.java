package org.vaslim.batch_stt.dto;

import jakarta.persistence.Column;
import org.vaslim.batch_stt.enums.ProcessingStatus;

import java.time.LocalDateTime;

public class ItemDTO {
    private Long id;

    private String filePathVideo;

    private String filePathText;

    private LocalDateTime processedTimestamp;

    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    private String videoFileName;

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getVideoFileName() {
        return videoFileName;
    }

    public void setVideoFileName(String videoFileName) {
        this.videoFileName = videoFileName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFilePathVideo() {
        return filePathVideo;
    }

    public void setFilePathVideo(String filePathVideo) {
        this.filePathVideo = filePathVideo;
    }

    public String getFilePathText() {
        return filePathText;
    }

    public void setFilePathText(String filePathText) {
        this.filePathText = filePathText;
    }

    public LocalDateTime getProcessedTimestamp() {
        return processedTimestamp;
    }

    public void setProcessedTimestamp(LocalDateTime processedTimestamp) {
        this.processedTimestamp = processedTimestamp;
    }
}
