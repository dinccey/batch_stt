package org.vaslim.batch_stt.service;

import org.springframework.stereotype.Service;
import org.vaslim.batch_stt.dto.AppUserDTO;

import java.util.Set;

@Service
public interface AdminService {

    AppUserDTO addUser(AppUserDTO appUserDTO);

    void removeUser(String username);

    AppUserDTO editUser(AppUserDTO appUserDTO);

    Set<AppUserDTO> getAllUsers();

    boolean isUserAdmin(String username);
}
