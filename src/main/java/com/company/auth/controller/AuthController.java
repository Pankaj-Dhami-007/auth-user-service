package com.company.auth.controller;

import com.company.auth.dtos.LoginRequest;
import com.company.auth.dtos.LoginResponse;
import com.company.auth.dtos.apiContract.ApiResponse;
import com.company.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/api/v1")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse =
                authService.login(request, response);

        return ResponseEntity.ok(
                ApiResponse.success(loginResponse)
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        LoginResponse loginResponse =
                authService.refreshToken(request, response);

        return ResponseEntity.ok(
                ApiResponse.success(loginResponse)
        );
    }

}
