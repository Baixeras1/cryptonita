package com.cryptonita.app;

import com.cryptonita.app.core.loaders.CoinLoader;
import com.cryptonita.app.core.loaders.UsersLoader;
import com.cryptonita.app.data.providers.ICoinProvider;
import com.cryptonita.app.data.providers.IStackingProvider;
import com.cryptonita.app.dto.integration.CoinInfoDTO;
import com.cryptonita.app.dto.response.UserResponseDTO;
import com.cryptonita.app.integration.services.IHistoryServiceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@Slf4j
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

    @Bean
    CommandLineRunner init(
            CoinLoader coinLoader,
            UsersLoader usersLoader,
            IStackingProvider stackingProvider,
            IHistoryServiceInfo serviceInfo
    ) {
        return (args) -> {
            Flux<CoinInfoDTO> coinFlux = coinLoader.load();
            Flux<UserResponseDTO> usersFlux = usersLoader.load();

            Flux.concat(coinFlux, usersFlux)
                    .doOnComplete(() -> {
                    })
                    .subscribe();

            serviceInfo.getAll("bitcoin","m1")
                    .subscribe(historyInfoDTO -> log.info(historyInfoDTO.toString()));

        };
    }
}
