
package com.fraudwatch.fraudruleengine.security;

import com.fraudwatch.fraudruleengine.entity.User;
import com.fraudwatch.fraudruleengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .accountExpired(!user.getEnabled())
                .accountLocked(!user.getEnabled())
                .credentialsExpired(!user.getEnabled())
                .disabled(!user.getEnabled())
                .build();
    }
}
