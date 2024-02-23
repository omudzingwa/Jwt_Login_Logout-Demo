package com.quad.login_logout.dto;

import lombok.Data;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
 /*   public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(username, password);
    }*/
    //The above toAuthentication method is used to create the UsernamePasswordAuthenticationToken required during the login process
}
