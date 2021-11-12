package com.epam.esm.service;

import com.epam.esm.entity.UserProfile;
import com.epam.esm.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDao dao;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserProfile userProfile = dao.findByLogin(login);
        if (userProfile == null) {
            throw new UsernameNotFoundException("Unknown user: " + login);
        }
        UserDetails user = User.builder()
                .username(userProfile.getLogin())
                .password(userProfile.getPassword())
                .roles(userProfile.getRole())
                .build();
        return user;
    }

    private Collection<GrantedAuthority> getGrantedAuthorities(UserProfile userProfile) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (userProfile.getRole().equals("ADMIN")) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return grantedAuthorities;
    }
}
