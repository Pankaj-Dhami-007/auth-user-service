package com.company.auth.service;

import com.company.auth.contextHolder.ClientContextHolder;
import com.company.auth.dtos.CreateAuthUserRequest;
import com.company.auth.dtos.CreateAuthUserResponse;
import com.company.auth.entity.RoleEntity;
import com.company.auth.entity.UserEntity;
import com.company.auth.repository.RoleEntityRepository;
import com.company.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleEntityRepository roleRepository;

    @Override
    public CreateAuthUserResponse createAuthUser(CreateAuthUserRequest request) {
        Long clientId = ClientContextHolder.getClientId();
        log.info("Starting user registration for username: {}, clientName: {}",
                request.getUsername(), clientId);

        try {
            // Validate & fetch roles for client
            Set<RoleEntity> roles = getValidRoles(
                    request.getRoles(),
                    clientId
            );

            UserEntity user = new UserEntity();
            user.setUsername(request.getUsername());
            //user.setEmail(request.getEmail());
            user.setClientId(clientId);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(roles);

            userAuthRepository.save(user);

            log.info("User and roles saved successfully. User ID: {}", user.getId());

            return new CreateAuthUserResponse(
                    user.getId(),
                    user.getUsername(),
                    "CREATED"
            );
        }
            catch(DataIntegrityViolationException e){
                log.error("Duplicate user registration attempt for username: {}, clientId: {}",
                        request.getUsername(), clientId, e);
                throw e;
            } catch(Exception e){
                log.error("Unhandled exception during registration for username: {}, clientId: {}",
                        request.getUsername(), clientId, e);
                throw e;
            }
        }

    private Set<RoleEntity> getValidRoles(
            Set<String> requestedRoles,
            Long clientId
    ) {
        // If no roles provided → assign default USER
        if (requestedRoles == null || requestedRoles.isEmpty()) {
            RoleEntity defaultRole = roleRepository
                    .findByClientIdAndRoleName(clientId, "USER")
                    .orElseThrow(() ->
                            new IllegalStateException("Default role USER not found"));
            return Set.of(defaultRole);
        }

        // Fetch roles for client
        List<RoleEntity> roles = roleRepository
                .findByClientIdAndRoleNameIn(clientId, requestedRoles);

        if (roles.size() != requestedRoles.size()) {
            throw new IllegalArgumentException("One or more roles are invalid for client");
        }

        return new HashSet<>(roles);
    }

}

