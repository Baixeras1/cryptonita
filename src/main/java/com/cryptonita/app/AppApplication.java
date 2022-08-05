package com.cryptonita.app;

import com.cryptonita.app.core.loaders.CoinLoader;
import com.cryptonita.app.core.loaders.UsersLoader;
import com.cryptonita.app.data.providers.IAccountProvider;
import com.cryptonita.app.data.providers.IRegisterProvider;
import com.cryptonita.app.dto.data.response.UserResponseDTO;
import com.cryptonita.app.dto.integration.CoinInfoDTO;
import com.cryptonita.app.dto.request.RegisterRequestDTO;
import com.cryptonita.app.integration.services.ICandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

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
            IAccountProvider accountProvider,
            IRegisterProvider registerProvider

    ) {
        return (args) -> {
            Flux<CoinInfoDTO> coinFlux = coinLoader.load();
            Flux<UserResponseDTO> usersFlux = usersLoader.load();

            Flux.concat(coinFlux, usersFlux)
                    .doOnComplete(() -> {
                        accountProvider.create("sergio.bernal","Bitcoin");
                        accountProvider.deposit("sergio.bernal","Bitcoin",12);

                        RegisterRequestDTO registerRequestDTO = RegisterRequestDTO.builder()
                                .date(LocalDate.now())
                                .quantity(12)
                                .destiny("Mi wallet")
                                .origin("Mi cartera")
                                .user("sergio.bernal")
                                .build();

                        registerProvider.log(registerRequestDTO);
                    })
                    .subscribe();


        };
    }

    private void callBack() {
    }

}
