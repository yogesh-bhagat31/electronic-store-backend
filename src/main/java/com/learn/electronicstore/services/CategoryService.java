package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.CategoryDto;
import com.learn.electronicstore.dtos.PageableResponse;

import java.util.List;

public interface CategoryService {

    // create
    CategoryDto create(CategoryDto categoryDto);

    //update
    CategoryDto update(CategoryDto categoryDto, String categoryId);

    //delete

    void delete(String categoryId);


    //get all
    PageableResponse<CategoryDto> getAll(int pageNumber,int pageSize ,String sortBy, String sortDir);

    //get single category detail
    CategoryDto get(String categoryId);

    //search
    List<CategoryDto> searchUser(String keyword);

}
