package com.cryptonita.app.core.controllers;

import com.cryptonita.app.core.controllers.services.IHistoryService;
import com.cryptonita.app.core.controllers.services.excel.ExcelGenerator;
import com.cryptonita.app.core.controllers.utils.RestResponse;
import com.cryptonita.app.dto.data.response.HistoryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/history")
@CrossOrigin("*")
@Tag(name = "History")
public class HistoryController {

    private final IHistoryService historyService;

    @GetMapping("/history")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get historical operations of the current user at a given date range")
    public RestResponse getHistoryByUserName(LocalDate start, LocalDate end) {
        return RestResponse.encapsulate(historyService.getAllRegisterUser(start, end));
    }

    @GetMapping("/download-to-excel")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get historical operations of the current user as a excel at a given date range (BETA)")
    public void downloadHistory(String start, String end, HttpServletResponse response) throws IOException {

        LocalDate localDateStart = LocalDate.parse(start);
        LocalDate localDateEnd = LocalDate.parse(end);

        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=history" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        List<HistoryResponseDTO> historyResponseDTOList = historyService.getAllRegisterUser(localDateStart, localDateEnd);

        ExcelGenerator generator = new ExcelGenerator(historyResponseDTOList);

        generator.generateExcelFile(response);

    }
}
