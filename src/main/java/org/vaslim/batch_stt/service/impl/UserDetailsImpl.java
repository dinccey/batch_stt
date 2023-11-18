package org.vaslim.batch_stt.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.vaslim.batch_stt.model.AppUser;

import java.io.Serial;
import java.util.Collection;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails
{
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;

    private final String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(AppUser appUser) {
        this.username = appUser.getUsername();
        this.password = appUser.getPassword();
    }

    public static UserDetailsImpl build(AppUser appUser) {

       return new UserDetailsImpl(appUser);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(user.password, username);
    }
}