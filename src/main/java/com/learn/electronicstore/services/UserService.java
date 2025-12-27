package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.PageableResponse;
import com.learn.electronicstore.dtos.UserDto;

import java.util.List;

public interface UserService {
    // Create
    UserDto createUser(UserDto userDto);

    // Update
    UserDto updateUser(UserDto userDto, String userId);

    // delete
    void deleteUser(String userId);

    // get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    // get single user by id
    UserDto getUserById(String userId);

    // get single user by email
    UserDto getUserByEmail(String email);

    // search user
    List<UserDto> searchUser(String keyword);

}
