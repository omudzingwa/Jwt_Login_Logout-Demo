package com.quad.login_logout.jwtutils;

import com.quad.login_logout.dto.LoginRequestDTO;
import com.quad.login_logout.dto.SignupRequestDTO;
import com.quad.login_logout.users.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class JwtAuthController {
    private final JwtAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Validated SignupRequestDTO signup, Errors errors) throws Exception {
        // validation check
        if (errors.hasErrors()) {
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(authService.signUp(signup),HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated LoginRequestDTO login, Errors errors) throws Exception {
        // validation check
        if (errors.hasErrors()) {
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(authService.login(login), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(String refresh, Errors errors) throws Exception {
        // validation check
        if (errors.hasErrors()) {
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(authService.reissueToken(refresh),HttpStatus.OK);
    }

   /* @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated LogoutDTO logout, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return new ResponseEntity<>(errors.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return authService.logout(logout);
    }*/


}
