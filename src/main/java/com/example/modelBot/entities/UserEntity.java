package com.example.modelBot.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEntity {
    @Id
    Long userId;
    String firstName;
    String userName;
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    List<PostEntity> posts;
    @CreationTimestamp
    Date createdAt;
}
