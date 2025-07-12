package com.unibox.dto;

import lombok.Data;

@Data
public class DepartmentLoginRequest {
    private String email;
    private String password;
}
