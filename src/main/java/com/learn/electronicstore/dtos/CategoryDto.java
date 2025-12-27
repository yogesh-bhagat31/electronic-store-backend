package com.learn.electronicstore.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
    private String categoryId;

    @Size(min = 4, message = "title must be of minimum 4 characters")
    @NotBlank(message = "title required !!")
    private String title;
    @NotBlank(message = "Description required!!")
    private String description;
    @NotBlank(message = "image required!!")
    private String coverImage;
}

