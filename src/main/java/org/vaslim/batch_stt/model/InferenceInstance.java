package org.vaslim.batch_stt.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inference_instance")
public class InferenceInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inferenceInstanceSeq")
    @SequenceGenerator(name = "inferenceInstanceSeq", sequenceName = "inference_instance_seq", allocationSize = 5)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(nullable = false)
    private String instanceUrl;

    @Column
    private Boolean available;

    @Column
    private Integer itemsProcessed;

    @Column
    private Integer failedRunsCount;

    @Column
    private Long totalProcessingTimeSeconds;

    @ManyToOne
    @JoinColumn(name="appUser", nullable=false)
    private AppUser appUser;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getItemsProcessed() {
        if(itemsProcessed == null) return 0;
        return itemsProcessed;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

    public Integer getFailedRunsCount() {
        if(failedRunsCount == null) return 0;
        return failedRunsCount;
    }

    public void setFailedRunsCount(Integer failedRunsCount) {
        this.failedRunsCount = failedRunsCount;
    }

    public Long getTotalProcessingTimeSeconds() {
        if(totalProcessingTimeSeconds == null) return 0L;
        return totalProcessingTimeSeconds;
    }

    public void setTotalProcessingTimeSeconds(Long totalProcessingTimeSeconds) {
        this.totalProcessingTimeSeconds = totalProcessingTimeSeconds;
    }
}
