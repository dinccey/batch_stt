package org.vaslim.batch_stt.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.vaslim.batch_stt.exception.BatchSttException;
import org.vaslim.batch_stt.model.AppUser;
import org.vaslim.batch_stt.repository.AppUserRepository;

@Component
public class AdminUserConfiguration {

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPlaintextPassword;

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminUserConfiguration(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional
    public void contextRefreshedEvent() {
        try {
            appUserRepository.removeAppUserByUsername(adminUsername);
            AppUser appUser = new AppUser();
            appUser.setAdmin(true);
            appUser.setPassword(passwordEncoder.encode(adminPlaintextPassword));
            appUser.setUsername(adminUsername);
            appUser.setItemsProcessed(0);
            appUserRepository.save(appUser);
        } catch (Exception e){
            e.printStackTrace();
            throw new BatchSttException(e.getMessage());
        }
    }
}
