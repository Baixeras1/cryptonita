package com.cryptonita.app.data.providers.impl;

import com.cryptonita.app.data.daos.ICoinDAO;
import com.cryptonita.app.data.entities.CoinModel;
import com.cryptonita.app.data.providers.ICoinProvider;
import com.cryptonita.app.data.providers.mappers.IMapper;
import com.cryptonita.app.dto.data.response.CoinResponseDTO;
import com.cryptonita.app.exceptions.data.CoinAlreadyExistsException;
import com.cryptonita.app.exceptions.data.CoinNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Table;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class CoinProviderImpl implements ICoinProvider {

    private static final String NO_COIN_FOUND = "The coin %s is not supported";
    private static final String COIN_ALREADY_EXISTS = "The coin %s already exists!";

    private final ICoinDAO coinDAO;
    private final IMapper<CoinModel, CoinResponseDTO> responseDTOIMapper;

    @Override
    public CoinResponseDTO createCoin(String name, String symbol, int rank) {
        if (coinDAO.findByName(name).isPresent())
            throw new CoinAlreadyExistsException(String.format(COIN_ALREADY_EXISTS, name));

        CoinModel coin = CoinModel.builder()
                .name(name)
                .symbol(symbol)
                .rank(rank)
                .build();

        coin = coinDAO.save(coin);

        return responseDTOIMapper.mapToDto(coin);
    }

    @Override
    public List<CoinResponseDTO> getAllCoins() {
        return coinDAO.findAll().stream()
                .map(responseDTOIMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    //@Transactional
    public CoinResponseDTO deleteByName(String name) {
        CoinModel coin = coinDAO.findByName(name)
                .orElseThrow(() -> new CoinNotFoundException(String.format(NO_COIN_FOUND, name)));

        coinDAO.deleteByName(name);

        return responseDTOIMapper.mapToDto(coin);
    }

    @Override
    public CoinResponseDTO getCoinByName(String name) {
        return coinDAO.findByName(name)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new CoinNotFoundException(String.format(NO_COIN_FOUND, name)));
    }

    @Override
    public CoinResponseDTO getCoinById(long id) {
        return coinDAO.findById(id)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new CoinNotFoundException(String.format(NO_COIN_FOUND, id)));
    }

    @Override
    public CoinResponseDTO getByRank(int rank) {
        return coinDAO.findByRank(rank)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new CoinNotFoundException(String.format(NO_COIN_FOUND, rank)));
    }

    @Override
    public CoinResponseDTO getBySymbol(String symbol) {
        return coinDAO.findBySymbol(symbol)
                .map(responseDTOIMapper::mapToDto)
                .orElseThrow(() -> new CoinNotFoundException(String.format(NO_COIN_FOUND, symbol)));
    }

}
