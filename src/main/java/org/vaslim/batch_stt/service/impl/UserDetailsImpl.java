package org.vaslim.batch_stt.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;

public class UserDetailsImpl implements UserDetails
{
    private static final long serialVersionUID = 1L;


    @Value("${spring.security.user.name}")
    private String username = "admin";

    @JsonIgnore
    @Value("${spring.security.user.password}")
    private String password = "$2a$10$RAn1Kc59SVdsBNLcrlDg5OCIYTZNu9WmRRx1wtpAqQ78eC6AMe4Oa";

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl() {
    }

    public static UserDetailsImpl build() {

       return new UserDetailsImpl();
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