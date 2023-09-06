package org.vaslim.batch_stt.model;


import jakarta.persistence.*;
import org.vaslim.batch_stt.enums.ProcessingStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemSeq")
    @SequenceGenerator(name = "itemSeq", sequenceName = "item_seq", allocationSize = 5)
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @Column(unique = true)
    private String filePathVideo;

    @Column(unique = true)
    private String filePathText;

    @Column
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus = ProcessingStatus.PENDING;

    @Column
    private String videoFileName;

    @Column
    private LocalDateTime processedTimestamp;

    @Column(length = 64)
    private String textFilterHash;

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

    public String getTextFilterHash() {
        return textFilterHash;
    }

    public void setTextFilterHash(String textFilterHash) {
        this.textFilterHash = textFilterHash;
    }
}
