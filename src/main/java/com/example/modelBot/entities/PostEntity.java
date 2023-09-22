package com.example.modelBot.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity(name = "posts")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long postId;
    String model;
    String pozitsiya;
    String paint;
    String color;
    String date_of;
    String millage;
    String fuel;
    String prise;
    String address;
    String contact;
    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    List<PhotosEntity> photos;
}
