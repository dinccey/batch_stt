package org.vaslim.batch_stt.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vaslim.batch_stt.exception.AppUserException;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.repository.AppUserRepository;


@Service
public class UserDetailsServiceImpl implements UserDetailsService
{

    private final AppUserRepository appUserRepository;

    public UserDetailsServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        AppUser appUser = appUserRepository.findByUsername(username).orElseThrow(()-> new AppUserException("Username not found: " + username));
        return UserDetailsImpl.build(appUser);
    }
}
