package com.learn.electronicstore.services;

import com.learn.electronicstore.dtos.RefreshTokenDto;
import com.learn.electronicstore.dtos.UserDto;

public interface RefreshTokenService {

    //create
    RefreshTokenDto createRefreshToken(String username);

    // find by token
    RefreshTokenDto findByToken(String token);

    //verify
    RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto);

    UserDto getUser(RefreshTokenDto dto);

}
