package com.example.modelBot.services;

import com.example.modelBot.entities.UserEntity;

import java.util.List;

public interface UserService {
    void addUser(Long userId,String firstName,String userName);
    void deleteUser(Long userId);
    void saveUser(UserEntity user);
    UserEntity getUserById(Long userId);
    List<UserEntity> getAll();
}
