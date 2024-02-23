package com.quad.login_logout.dto;

import com.quad.login_logout.roles.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignupRequestDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Role role;
}
