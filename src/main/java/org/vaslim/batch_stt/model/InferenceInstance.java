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
    private Integer itemsProcessed = 0;

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
        return itemsProcessed;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }
}
