package com.example.bankcards.security.userDetails;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserIsNotEnabledException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with name %s not found", username)
                ));

        if (!user.getEnabled()) {
            throw new UserIsNotEnabledException(
                    String.format("User %d is not enabled", user.getId())
            );
        }

        return new CustomUserDetails(user);
    }

}
