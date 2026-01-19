package com.company.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAuthUserResponse {

    private Long userId;
    private String username;
    private String status; // CREATED
}
