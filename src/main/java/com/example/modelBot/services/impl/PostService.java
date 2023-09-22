package com.example.modelBot.services.impl;

import com.example.modelBot.entities.DTO.PostDTO;
import com.example.modelBot.entities.PhotosEntity;
import com.example.modelBot.entities.PostEntity;
import com.example.modelBot.entities.UserEntity;
import com.example.modelBot.services.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements PostsService {
    private final UserServiceImpl userService;

    @Override
    public void addPost(PostDTO postEntity, Long userId) {
        UserEntity user = userService.getUserById(userId);
        PostEntity post;
        List<PostEntity> posts;
        if(user.getPosts() == null){
            posts = new ArrayList<>();
            post = PostEntity.builder()
                    .model(postEntity.getModel())
                    .color(postEntity.getColor())
                    .paint(postEntity.getPaint())
                    .date_of(postEntity.getDate_of())
                    .prise(postEntity.getPrise())
                    .millage(postEntity.getMillage())
                    .fuel(postEntity.getFuel())
                    .contact(postEntity.getContact())
                    .pozitsiya(postEntity.getPozitsiya())
                    .address(postEntity.getAddress())
                    .photos(postEntity.getPhotos().stream().map(photo -> PhotosEntity.builder().photo(photo).build()).collect(Collectors.toList()))
                    .build();
        }else {
            posts = user.getPosts();
            post = PostEntity.builder()
                    .model(postEntity.getModel())
                    .color(postEntity.getColor())
                    .paint(postEntity.getPaint())
                    .date_of(postEntity.getDate_of())
                    .prise(postEntity.getPrise())
                    .millage(postEntity.getMillage())
                    .fuel(postEntity.getFuel())
                    .contact(postEntity.getContact())
                    .pozitsiya(postEntity.getPozitsiya())
                    .address(postEntity.getAddress())
                    .photos(postEntity.getPhotos().stream().map(photo -> PhotosEntity.builder().photo(photo).build()).collect(Collectors.toList()))
                    .build();
        }
        posts.add(post);
        user.setPosts(posts);
        userService.saveUser(user);
    }
}
