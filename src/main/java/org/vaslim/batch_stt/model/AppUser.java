package org.vaslim.batch_stt.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column
    private String password;

    @Column
    private boolean admin = false;

    @Column
    private Integer itemsProcessed;

    @Column
    private Long totalProcessingTimeSeconds;


    @OneToMany(mappedBy="appUser", cascade = CascadeType.ALL)
    private Set<InferenceInstance> inferenceInstances;

    public Integer getItemsProcessed() {
        if(itemsProcessed == null) return 0;
        return itemsProcessed;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<InferenceInstance> getInferenceInstances() {
        return inferenceInstances;
    }

    public void setInferenceInstances(Set<InferenceInstance> inferenceInstances) {
        this.inferenceInstances = inferenceInstances;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public Long getTotalProcessingTimeSeconds() {
        if(totalProcessingTimeSeconds == null) return 0L;
        return totalProcessingTimeSeconds;
    }

    public void setTotalProcessingTimeSeconds(Long totalProcessingTimeSeconds) {
        this.totalProcessingTimeSeconds = totalProcessingTimeSeconds;
    }
}
