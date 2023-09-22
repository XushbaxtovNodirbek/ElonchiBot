package com.example.modelBot.repos;

import com.example.modelBot.entities.PostEntity;
import com.example.modelBot.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity,Long> {

}
