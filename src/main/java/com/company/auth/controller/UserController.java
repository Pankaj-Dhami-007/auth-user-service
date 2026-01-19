package com.company.auth.controller;

import com.company.auth.dtos.CreateAuthUserRequest;
import com.company.auth.dtos.CreateAuthUserResponse;
import com.company.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/user/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService authUserService;

    @PostMapping("/create")
    public ResponseEntity<CreateAuthUserResponse> createAuthUser(
            @RequestBody CreateAuthUserRequest request
    ) {
        log.info("🔵 CREATE_AUTH_USER_REQUEST | Starting to create auth user | Username: {}",
                request.getUsername());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authUserService.createAuthUser(request));
    }
}

