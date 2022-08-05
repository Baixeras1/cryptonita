package com.cryptonita.app.integration.adapters.mappers;

import com.cryptonita.app.dto.integration.CoinInfoDTO;
import com.cryptonita.app.dto.integration.HistoryInfoDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class HistoryAdapterMapper implements AdapterMapper<HistoryInfoDTO>{

    private final ObjectMapper jsonMapper;

    @SneakyThrows
    @Override
    public HistoryInfoDTO mapToDto(String s) {
        JsonNode jsonNode = jsonMapper.readTree(s);
        JsonNode data = jsonNode.get("data");

        return maper(data);
    }

    @SneakyThrows
    @Override
    public List<HistoryInfoDTO> mapManyToDto(String s) {
        JsonNode jsonNode = jsonMapper.readTree(s);
        ArrayNode data = (ArrayNode) jsonNode.get("data");

        List<HistoryInfoDTO> historys = new ArrayList<>();
        data.forEach(node -> historys.add(maper(node)));

        return historys;
    }


    private HistoryInfoDTO maper(JsonNode node) {
        return HistoryInfoDTO.builder()
                .priceUsd(node.get("priceUsd").asDouble())
                .time(node.get("time").asDouble())
                .build();
    }
}