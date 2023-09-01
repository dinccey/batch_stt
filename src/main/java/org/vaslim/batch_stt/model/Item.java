package org.vaslim.batch_stt.model;


import jakarta.persistence.*;

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
