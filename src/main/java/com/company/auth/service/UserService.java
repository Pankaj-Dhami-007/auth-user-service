package com.company.auth.service;

import com.company.auth.dtos.CreateAuthUserRequest;
import com.company.auth.dtos.CreateAuthUserResponse;

public interface UserService {
    CreateAuthUserResponse createAuthUser(CreateAuthUserRequest createAuthUserRequest);
}
