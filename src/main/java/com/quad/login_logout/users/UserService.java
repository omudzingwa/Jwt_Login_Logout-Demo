package com.quad.login_logout.users;

import com.quad.login_logout.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public User saveUser(User user){
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    public List<User> listAllUsers(){
        return userRepository.findAll();
    }
    public void deleteUser(long id){
        userRepository.deleteById(id);
    }

    public Optional<User> findById(long id){
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username){
        return userRepository.findUserByUsername(username);
    }

    public Role getUserRole(long id){
        return userRepository.getUserRole(id);
    }

}
