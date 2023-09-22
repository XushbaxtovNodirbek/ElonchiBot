package com.example.modelBot.repos;

import com.example.modelBot.entities.PhotosEntity;
import com.example.modelBot.entities.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotosEntity,Long> {

}
