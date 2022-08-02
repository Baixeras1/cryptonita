package com.cryptonita.app.core.controllers;

import com.cryptonita.app.core.controllers.services.IPorfolioService;
import com.cryptonita.app.dto.response.WallerResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/api/porfolio")
public class PorfolioController {

    private final IPorfolioService porfolioService;

    @GetMapping("/get")
    public WallerResponseDto get(String user,String coin) {
        return porfolioService.get(user,coin);
    }

    @GetMapping("/getAll")
    public List<WallerResponseDto> getAll(String user) {
        return porfolioService.getAll(user);
    }


}