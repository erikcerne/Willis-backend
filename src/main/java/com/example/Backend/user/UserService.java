package com.example.Backend.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User registerIfNew(String auth0Id, String email) {
        return repo.findByIdOrNot(auth0Id).orElseGet(() -> {
            User newUser = new User();
            newUser.setUserId(auth0Id);
            newUser.setEmail(email);
            return repo.save(newUser);
        });
    }

}
