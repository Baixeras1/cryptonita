package com.cryptonita.app.data.providers.mappers.impl;

import com.cryptonita.app.data.entities.FavouritesModel;
import com.cryptonita.app.data.entities.UserModel;
import com.cryptonita.app.data.entities.WalletModel;
import com.cryptonita.app.data.providers.mappers.IMapper;
import com.cryptonita.app.dto.response.FavoritesResponseDto;
import com.cryptonita.app.dto.response.UserResponseDTO;
import com.cryptonita.app.dto.response.WallerResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserResponseMapper implements IMapper<UserModel, UserResponseDTO> {

    private final IMapper<WalletModel, WallerResponseDto> walletResponseDtoMapper;

    private final IMapper<FavouritesModel, FavoritesResponseDto> favoritesResponseDTOMapper;
    @Override
    public UserResponseDTO mapToDto(UserModel userModel) {
        List<WallerResponseDto> wallerResponseDtos = userModel.getAccount().getWalletModels().stream()
                .map(walletResponseDtoMapper::mapToDto)
                .collect(Collectors.toList());

        List<FavoritesResponseDto> favoritesResponseDtos = userModel.getFavourites().stream()
                .map(favoritesResponseDTOMapper::mapToDto)
                .collect(Collectors.toList());

        return UserResponseDTO.builder()
                .mail(userModel.getMail())
                .username(userModel.getUsername())
                .role(userModel.getRole())
                .type(userModel.getType())
                .favorites(favoritesResponseDtos)
                .wallet(wallerResponseDtos)
                .build();
    }

    @Override
    public UserModel mapToEntity(UserResponseDTO userResponseDTO) {
        throw new UnsupportedOperationException();
    }
}
