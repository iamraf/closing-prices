package io.iamraf.closingprices.util;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtils {

    public static long getLastOccurrenceOfTimeInET(int hour, int minute) {
        var easternZone = ZoneId.of("America/New_York");
        var nowInET = ZonedDateTime.now(easternZone);
        var today = nowInET.toLocalDate();
        var targetTime = LocalTime.of(hour, minute);
        var targetDateTimeToday = ZonedDateTime.of(today, targetTime, easternZone);

        if (targetDateTimeToday.isAfter(nowInET)) {
            var targetDateTimeYesterday = targetDateTimeToday.minusDays(1);
            return targetDateTimeYesterday.toInstant().toEpochMilli();
        }

        return targetDateTimeToday.toInstant().toEpochMilli();
    }

}
