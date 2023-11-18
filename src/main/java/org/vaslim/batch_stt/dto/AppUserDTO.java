package org.vaslim.batch_stt.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AppUserDTO {
    private String username;

    private String password;

    private boolean admin = false;

    private Integer itemsProcessed = 0;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
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
}
