package com.example.modelBot.entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
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
    List<String> photos;
}
