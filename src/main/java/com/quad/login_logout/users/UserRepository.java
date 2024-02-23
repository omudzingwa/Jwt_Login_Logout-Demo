package com.quad.login_logout.users;

import com.quad.login_logout.roles.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Override
    Optional<User> findById(Long id);
    Optional<User> findUserByUsername(String username);

    @Query(value = "select role from users where id = ?1",nativeQuery = true)
    Role getUserRole(long id);
}
