package com.cryptonita.app.data.providers.mappers.impl;

import com.cryptonita.app.data.providers.mappers.IMapper;
import com.cryptonita.app.dto.data.response.CoinDetailsDTO;
import com.cryptonita.app.dto.data.response.PorfolioResponseDTO;
import com.cryptonita.app.dto.data.response.WalletResponseDto;
import com.cryptonita.app.integration.services.ICoinMarketService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PorfolioResponseMapper implements IMapper<Map<String,WalletResponseDto>, PorfolioResponseDTO> {

    private final ICoinMarketService marketService;

    @Override
    public PorfolioResponseDTO mapToDto(Map<String, WalletResponseDto> wallets) {
        List<CoinDetailsDTO> coinDetailsDTOList = wallets.values().stream()
                .map(this::mapToDetails)
                .collect(Collectors.toList());

        double totalBalance = calculateBalance(coinDetailsDTOList);

        calculateAllocation(coinDetailsDTOList, totalBalance);

        return PorfolioResponseDTO.builder()
                .balance(totalBalance)
                .coinDetailsDTOList(coinDetailsDTOList)
                .build();
    }

    @Override
    public Map<String, WalletResponseDto> mapToEntity(PorfolioResponseDTO porfolioResponseDTO) {
        throw new RuntimeException();
    }

    private CoinDetailsDTO mapToDetails(WalletResponseDto dto) {
        return CoinDetailsDTO.builder()
                .id(dto.getId())
                .name(dto.getCoinName())
                .quantity(dto.getQuantity())
                .coinMarketDTO(marketService.getCoinMetadataByName(dto.getCoinName()).block())
                .build();
    }

    public double calculateBalance(List<CoinDetailsDTO> coinDetailsDTOList) {
        return coinDetailsDTOList.stream()
                .map(coinDetailsDTO -> coinDetailsDTO.getQuantity() * coinDetailsDTO.getCoinMarketDTO().priceUsd)
                .reduce(Double::sum)
                .orElse(0.0);

    }

    private void calculateAllocation(List<CoinDetailsDTO> coinDetailsDTOList, double totalBalance) {
        for (CoinDetailsDTO coinDetailsDTO : coinDetailsDTOList) {
            double individualPrice = coinDetailsDTO.getQuantity() * coinDetailsDTO.getCoinMarketDTO().priceUsd;
            double allocation = (individualPrice / totalBalance) * 100;

            coinDetailsDTO.setAllocation(allocation);
        }
    }
}