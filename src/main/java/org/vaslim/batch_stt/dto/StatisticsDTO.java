package org.vaslim.batch_stt.dto;

import java.util.Set;

public class StatisticsDTO {

    private Integer itemsProcessed;

    private Integer pendingItemsTotal;

    private Integer processedItemsTotal;
    Set<InferenceInstanceDTO> inferenceInstances;

    public Integer getItemsProcessed() {
        return itemsProcessed;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

    public Integer getPendingItemsTotal() {
        return pendingItemsTotal;
    }

    public void setPendingItemsTotal(Integer pendingItemsTotal) {
        this.pendingItemsTotal = pendingItemsTotal;
    }

    public Integer getProcessedItemsTotal() {
        return processedItemsTotal;
    }

    public void setProcessedItemsTotal(Integer processedItemsTotal) {
        this.processedItemsTotal = processedItemsTotal;
    }

    public Set<InferenceInstanceDTO> getInferenceInstances() {
        return inferenceInstances;
    }

    public void setInferenceInstances(Set<InferenceInstanceDTO> inferenceInstances) {
        this.inferenceInstances = inferenceInstances;
    }
}
