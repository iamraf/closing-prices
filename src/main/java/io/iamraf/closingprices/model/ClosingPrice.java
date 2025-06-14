package io.iamraf.closingprices.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public record ClosingPrice(String symbol,
                           ZonedDateTime openTime,
                           BigDecimal open,
                           BigDecimal high,
                           BigDecimal low,
                           BigDecimal close,
                           ZonedDateTime closeTime) {

    public static ClosingPrice of(String symbol, List<Object> data) {
        return new ClosingPrice(
                symbol,
                Instant.ofEpochMilli(((Number) data.get(0)).longValue()).atZone(ZoneId.of("America/New_York")),
                BigDecimal.valueOf(Double.parseDouble((String) data.get(1))),
                BigDecimal.valueOf(Double.parseDouble((String) data.get(2))),
                BigDecimal.valueOf(Double.parseDouble((String) data.get(3))),
                BigDecimal.valueOf(Double.parseDouble((String) data.get(4))),
                Instant.ofEpochMilli(((Number) data.get(6)).longValue()).atZone(ZoneId.of("America/New_York"))
        );
    }

    public static ClosingPrice ofEmpty(String symbol) {
        return new ClosingPrice(
                symbol,
                Instant.parse("1900-01-01T00:00:00Z").atZone(ZoneId.of("America/New_York")),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("9999-12-31T23:59:59Z").atZone(ZoneId.of("America/New_York"))
        );
    }

}
