package com.quad.login_logout.dto;

import lombok.Data;

@Data
public class RefreshTokenDTO {
    private String accessToken;
    private String refreshToken;
}
