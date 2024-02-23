package com.quad.login_logout.jwtutils;

import com.quad.login_logout.dto.LoginRequestDTO;
import com.quad.login_logout.dto.LoginResponseDTO;
import com.quad.login_logout.dto.SignupRequestDTO;
import com.quad.login_logout.dto.SignupResponseDTO;
import com.quad.login_logout.roles.Role;
import com.quad.login_logout.users.User;
import com.quad.login_logout.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class JwtAuthService{

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDTO signUp(SignupRequestDTO signupRequest) throws Exception {
        String userTosave = signupRequest.getUsername();
        Optional<User> userTofind = userRepository.findUserByUsername(userTosave);
        if(userTofind.isPresent()){
            throw new Exception("Username is already in database, duplicates not allowed");
        }
        User user = User.builder()
                .firstname(signupRequest.getFirstname())
                .lastname(signupRequest.getLastname())
                .username(signupRequest.getUsername())
                //.password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return SignupResponseDTO.signupResponse(user);
    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) throws Exception {
        try {
            if(loginRequest.getUsername()==null){
                log.info("Error detected, username is null");
                throw new Exception("Username not found");
            }

            Authentication authRequest = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    jwtTokenProvider.createAccessToken(authRequest),
                    jwtTokenProvider.createRefreshToken(authRequest)
            );

            return loginResponse;

        }catch(BadCredentialsException e){
            log.error("Invalid Password : " +e.getMessage());
            throw new Exception("Invalid user password");
        }
    }


    @Transactional
    public LoginResponseDTO reissueToken(String refreshToken) throws Exception {
        // Refresh Token Verification
        jwtTokenProvider.validateToken(refreshToken);

        // Get Username from Access Token
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);

        // Retrieve the Refresh Token value saved from Redis
        String redisRefreshToken = redisTemplate.opsForValue().get(authentication.getName());
        if(!redisRefreshToken.equals(refreshToken)) {
            throw new Exception("Refresh token does not exist or has expired. Please log in again");
        }
        // Token reissue
        LoginResponseDTO loginResponse = new LoginResponseDTO(
                jwtTokenProvider.createAccessToken(authentication),
                jwtTokenProvider.createRefreshToken(authentication)
        );

        return loginResponse;
    }

}
