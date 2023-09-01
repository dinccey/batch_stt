package org.vaslim.batch_stt.dto;

public class ItemDTO {
    private Long id;

    private String filePathVideo;

    private String filePathText;

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
}
