package io.iamraf.closingprices.configuration.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

public interface BinanceRestClient {

    @GetExchange(url = "/api/v3/klines?symbol={symbol}&interval=1m&limit=1&startTime={timestamp}")
    List<List<Object>> getPrices(@PathVariable("symbol") String symbol,
                                 @PathVariable("timestamp") Long timestamp);

}
