package io.iamraf.closingprices.service;

import io.iamraf.closingprices.configuration.client.BinanceRestClient;
import io.iamraf.closingprices.configuration.properties.ServiceProperties;
import io.iamraf.closingprices.model.ClosingPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static io.iamraf.closingprices.util.TimeUtils.getLastOccurrenceOfTimeInET;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClosingPricesService {

    private final Map<String, Map<String, ClosingPrice>> closingPrices = new ConcurrentHashMap<>();
    private final BinanceRestClient restClient;

    public void refresh(ServiceProperties.Job job) {
        var currentPrices = job.symbols().stream()
                .collect(Collectors.toConcurrentMap(
                        value -> value,
                        value -> loadLatestPrice(value, Integer.valueOf(job.id()))
                ));

        closingPrices.put(job.id(), currentPrices);

        log.info("Refreshed closing prices for job [{}]", job.id());
    }

    public List<ClosingPrice> getLatestPrices(String time) {
        var prices = closingPrices.get(time);

        if (isNull(prices)) {
            return Collections.emptyList();
        }

        return prices.values().stream()
                .sorted(Comparator.comparing(ClosingPrice::close))
                .toList()
                .reversed();
    }

    private ClosingPrice loadLatestPrice(String symbol, Integer time) {
        Optional<ClosingPrice> resource = Optional.empty();

        try {
            resource = restClient.getPrices(symbol, getLastOccurrenceOfTimeInET(time, 0)).stream()
                    .map(objectList -> ClosingPrice.of(symbol, objectList))
                    .findFirst();

            if (resource.isEmpty()) {
                log.warn("No resource found for symbol [{}] and time [{}]", symbol, time);
            }
        } catch (Exception e) {
            log.error("Error retrieving latest price for symbol [{}]", symbol, e);
        }

        return resource.orElse(ClosingPrice.ofEmpty(symbol));
    }

}
