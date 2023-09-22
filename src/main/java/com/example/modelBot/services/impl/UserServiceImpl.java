package com.example.modelBot.services.impl;

import com.example.modelBot.entities.UserEntity;
import com.example.modelBot.repos.UserRepository;
import com.example.modelBot.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void addUser(Long userId,String firstName, String userName) {
        UserEntity user = UserEntity.builder()
                .firstName(firstName)
                .userId(userId)
                .userName(userName)
                .build();
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void saveUser(UserEntity user) {
        userRepository.save(user);
    }

    @Override
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public List<UserEntity> getAll() {
        return userRepository.findAll();
    }
}
