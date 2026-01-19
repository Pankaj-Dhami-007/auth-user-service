package com.company.auth.service;

import com.company.auth.dtos.LoginRequest;
import com.company.auth.dtos.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    LoginResponse login(LoginRequest loginRequest, HttpServletResponse response);
    LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response);
}
