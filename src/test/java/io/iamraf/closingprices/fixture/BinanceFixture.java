package io.iamraf.closingprices.fixture;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BinanceFixture {

    public static List<Object> ofBtc() {
        var time = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                ZoneId.of("America/New_York"));

        return of(
                time.toInstant().toEpochMilli(),
                "100000",
                "105000",
                "95000",
                "103000",
                "",
                time.plusSeconds(59).plusNanos(999).toInstant().toEpochMilli()
        );
    }

    public static List<Object> ofEth() {
        var time = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 12, 0, 0),
                ZoneId.of("America/New_York"));

        return of(
                time.toInstant().toEpochMilli(),
                "2500",
                "2700",
                "2300",
                "2550",
                "",
                time.plusSeconds(59).plusNanos(999).toInstant().toEpochMilli()
        );
    }

    public static List<Object> ofSol() {
        var time = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 17, 0, 0),
                ZoneId.of("America/New_York"));

        return of(
                time.toInstant().toEpochMilli(),
                "140",
                "155",
                "137",
                "145",
                "",
                time.plusSeconds(59).plusNanos(999).toInstant().toEpochMilli()
        );
    }

    public static List<Object> ofXrp() {
        var time = ZonedDateTime.of(LocalDateTime.of(2025, 1, 1, 17, 0, 0),
                ZoneId.of("America/New_York"));

        return of(
                time.toInstant().toEpochMilli(),
                "2.10",
                "2.30",
                "2.10",
                "2.20",
                "",
                time.plusSeconds(59).plusNanos(999).toInstant().toEpochMilli()
        );
    }

    public static List<Object> of(long openTime,
                                  String open,
                                  String high,
                                  String low,
                                  String close,
                                  String volume,
                                  long closeTime) {
        return List.of(
                openTime,
                open,
                high,
                low,
                close,
                volume,
                closeTime
        );
    }

}
