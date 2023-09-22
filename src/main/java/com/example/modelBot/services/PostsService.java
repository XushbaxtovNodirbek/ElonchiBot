package com.example.modelBot.services;

import com.example.modelBot.entities.DTO.PostDTO;

public interface PostsService {
    void addPost(PostDTO postEntity, Long userId);
}
