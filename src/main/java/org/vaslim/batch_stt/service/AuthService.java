package org.vaslim.batch_stt.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthService
{
    UserDetails authenticate(String username, String password);
}
