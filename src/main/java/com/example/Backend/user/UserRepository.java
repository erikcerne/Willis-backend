package com.example.Backend.user;

import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {

    JpaUserRepository jpa;

    public UserRepository(JpaUserRepository jpa) {
        this.jpa = jpa;
    }
    public User findById(String id){
        return jpa.findById(id).orElseThrow(()-> new NoSuchElementException("användare hittades inte"));
    }
    public Optional<User> findByIdOrNot(String id) {
        return jpa.findById(id);
    }

    public User save(User user) {
        return jpa.save(user);
    }

}
