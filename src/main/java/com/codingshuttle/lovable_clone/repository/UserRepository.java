package com.codingshuttle.lovable_clone.repository;

import com.codingshuttle.lovable_clone.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
