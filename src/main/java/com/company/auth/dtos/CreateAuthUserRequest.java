package com.company.auth.dtos;

import lombok.Data;

import java.util.Set;

@Data
public class CreateAuthUserRequest {
    private String username;
    private String password;
    private Set<String> roles;
}
