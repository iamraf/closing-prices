package io.iamraf.closingprices.controller;

import io.iamraf.closingprices.model.ClosingPrice;
import io.iamraf.closingprices.service.ClosingPricesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("prices")
@RequiredArgsConstructor
@Slf4j
public class ClosingPricesController {

    private final ClosingPricesService closingPricesService;

    @GetMapping
    public ResponseEntity<List<ClosingPrice>> getClosingPrices(@RequestParam("time") String time) {
        var prices = closingPricesService.getLatestPrices(time);

        return ResponseEntity.ok().body(prices);
    }

}
