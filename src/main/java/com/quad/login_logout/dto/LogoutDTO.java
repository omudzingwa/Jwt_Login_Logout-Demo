package com.quad.login_logout.dto;

import lombok.Data;

@Data
public class LogoutDTO {
    private String accessToken;
    private String refreshToken;
}
