package com.example.Backend.user;

import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.UUID;

@Repository
public class UserRepository {

    JpaUserRepository jpa;

    public UserRepository(JpaUserRepository jpa) {
        this.jpa = jpa;
    }
    public User getUserById(UUID id){
        return jpa.findById(id).orElseThrow(()-> new NoSuchElementException("användare hittades inte"));
    }
}
