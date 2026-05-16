package com.example.jwt_demo.service;

import com.example.jwt_demo.Entity.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;
@Service
public class CustomUserDetailsService  implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByGmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with username: " + username);
        }
        return new CustomUserDetails(
                user.getId(),
                user.getGmail(),
                user.getName(),
                user.getRole(),
                user.getPassword()
        );
    }
}
