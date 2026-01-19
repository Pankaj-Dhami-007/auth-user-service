package com.company.auth.security;

import com.company.auth.entity.UserEntity;
import com.company.auth.enums.ResponseErrorCodes;
import com.company.auth.exceptions.ResourceNotFoundException;
import com.company.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userAuthRepository
                .findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ResponseErrorCodes.USER_NOT_FOUND
                        )
                );
        return new CustomUserDetails(user);
    }
}
