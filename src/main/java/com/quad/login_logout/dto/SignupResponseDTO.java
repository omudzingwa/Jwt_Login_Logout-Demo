package com.quad.login_logout.dto;

import com.quad.login_logout.roles.Role;
import com.quad.login_logout.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SignupResponseDTO {
    private String firstname;
    private String lastname;
    private String username;
    private Role role;
    public static SignupResponseDTO signupResponse(User user){
        return SignupResponseDTO.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
