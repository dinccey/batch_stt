package org.vaslim.batch_stt.dto;

import jakarta.validation.constraints.NotNull;

public class InferenceInstanceDTO {

    private Long id;

    @NotNull
    private String instanceUrl;

    private Boolean available;

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
}
