package com.cryptonita.app.core.controllers.services;

import com.cryptonita.app.dto.response.UserResponseDTO;
import com.cryptonita.app.dto.response.WallerResponseDto;

import java.util.List;

/**
 * This class represents all the relevant methods to get information about User and Account from the database.
 */
public interface IPorfolioService {
    WallerResponseDto get(String user, String coin);

    List<WallerResponseDto> getAll(String user);

}