package com.epam.gymapp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epam.gymapp.model.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Finds a user by their username and returns an Optional
    Optional<User> findByUsername(String username);
}