package com.cryptonita.app.integration.adapters;

import com.cryptonita.app.dto.integration.CoinInfoDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICoinAdapter {

    /**
     * Retrieves the most important cryptos and the
     * basic information of them
     *
     * @return a reactive flux with the dtos carrying the info
     */
    Flux<CoinInfoDTO> getAll();

    /**
     * Retrieves the information of one crypto in specific
     *
     * @param symbol the symbol of the coin to search
     * @return a reactive mono with the information wrapped in a dto
     */
    Mono<CoinInfoDTO> getBySymbol(String symbol);

    /**
     * Retrieves the information of one crypto in specific
     *
     * @param symbol the name of the coin to search
     * @return a reactive mono with the information wrapped in a dto
     */
    Mono<CoinInfoDTO> getByName(String symbol);

    /**
     * Retrieves the information of one crypto in specific
     *
     * @param rank the rank of the coin to search
     * @return a reactive mono with the information wrapped in a dto
     */
    Mono<CoinInfoDTO> getByRank(int rank);

}