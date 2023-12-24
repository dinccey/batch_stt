package org.vaslim.batch_stt.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaslim.batch_stt.dto.AppUserDTO;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.repository.AppUserRepository;
import org.vaslim.batch_stt.service.AdminService;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AppUserRepository appUserRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(AppUserRepository appUserRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AppUserDTO addUser(AppUserDTO appUserDTO) {
        AppUser appUser = modelMapper.map(appUserDTO, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));
        appUser.setAdmin(false);

        return modelMapper.map(appUserRepository.save(appUser), AppUserDTO.class);
    }

    @Transactional
    @Override
    public void removeUser(String username) {
        appUserRepository.removeAppUserByUsername(username);
    }

    @Override
    public AppUserDTO editUser(AppUserDTO appUserDTO) {
        AppUser appUser = modelMapper.map(appUserDTO, AppUser.class);
        appUser.setPassword(passwordEncoder.encode(appUserDTO.getPassword()));

        return modelMapper.map(appUserRepository.save(appUser), AppUserDTO.class);
    }

    @Override
    public Set<AppUserDTO> getAllUsers() {
        return appUserRepository.findAll().stream().map(appUser -> modelMapper.map(appUser, AppUserDTO.class)).collect(Collectors.toSet());
    }

    @Override
    public boolean isUserAdmin(String username) {
        return appUserRepository.findByUsernameAndAdminTrue(username).isPresent();
    }
}
