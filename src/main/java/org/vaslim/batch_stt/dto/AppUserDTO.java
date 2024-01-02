package org.vaslim.batch_stt.dto;

public class AppUserDTO {
    private String username;

    private String password;

    private boolean admin = false;

    private Integer itemsProcessed = 0;

    private Long totalProcessingTimeSeconds;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public void setItemsProcessed(Integer itemsProcessed) {
        this.itemsProcessed = itemsProcessed;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Integer getItemsProcessed() {
        return itemsProcessed;
    }

    public Long getTotalProcessingTimeSeconds() {
        return totalProcessingTimeSeconds;
    }

    public void setTotalProcessingTimeSeconds(Long totalProcessingTimeSeconds) {
        this.totalProcessingTimeSeconds = totalProcessingTimeSeconds;
    }
}
