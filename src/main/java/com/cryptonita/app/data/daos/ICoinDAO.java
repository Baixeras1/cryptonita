package com.cryptonita.app.data.daos;

import com.cryptonita.app.data.entities.CoinModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ICoinDAO extends JpaRepository<CoinModel,Long> {

    @Query("SELECT c from CoinModel c WHERE lower(c.name) LIKE lower(?1)")
    Optional<CoinModel> findByName(String name);

    @Query("DELETE from CoinModel c WHERE lower(c.name) LIKE lower(?1)")
    void deleteByName(String name);

    Optional<CoinModel> findByRank(int rank);

    @Query("SELECT c from CoinModel c WHERE upper(c.symbol) LIKE upper(?1)")
    Optional<CoinModel> findBySymbol(String symbol);

}
