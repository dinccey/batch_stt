package org.vaslim.batch_stt.dto;

import jakarta.validation.constraints.NotNull;

public class InferenceInstanceDTO {

    private Long id;

    @NotNull
    private String instanceUrl;

    private Boolean available;

    private Integer itemsProcessed;
    private Integer failedRunsCount;
    private Long totalProcessingTimeSeconds;

    public Integer getItemsProcessed() {
        return itemsProcessed;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
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

    public Integer getFailedRunsCount() {
        return failedRunsCount;
    }

    public void setFailedRunsCount(Integer failedRunsCount) {
        this.failedRunsCount = failedRunsCount;
    }

    public Long getTotalProcessingTimeSeconds() {
        return totalProcessingTimeSeconds;
    }

    public void setTotalProcessingTimeSeconds(Long totalProcessingTimeSeconds) {
        this.totalProcessingTimeSeconds = totalProcessingTimeSeconds;
    }
}
