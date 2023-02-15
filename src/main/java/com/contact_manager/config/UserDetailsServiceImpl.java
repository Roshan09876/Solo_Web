package com.contact_manager.config;

import com.contact_manager.repo.UserRepository;
import com.contact_manager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //fetching user form database
        User user = userRepository.getUserByUserName(username);
        if(user==null){
            throw new UsernameNotFoundException("Could not Found User");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        return customUserDetails;
    }
}
