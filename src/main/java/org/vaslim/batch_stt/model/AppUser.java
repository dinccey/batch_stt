package org.vaslim.batch_stt.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "appUserSeq")
    @SequenceGenerator(name = "appUserSeq", sequenceName = "app_user_seq", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column
    private String username;

    @Column
    private String password;

    @Column(updatable = false)
    private boolean admin = false;

    @Column
    private Integer itemsProcessed = 0;

    @OneToMany(mappedBy="appUser", cascade = CascadeType.ALL)
    private Set<InferenceInstance> inferenceInstances;

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
}
