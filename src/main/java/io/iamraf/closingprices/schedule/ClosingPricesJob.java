package io.iamraf.closingprices.schedule;

import io.iamraf.closingprices.configuration.properties.ServiceProperties;
import io.iamraf.closingprices.service.ClosingPricesService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClosingPricesJob {

    private final ClosingPricesService closingPricesService;
    private final TaskScheduler refreshTaskScheduler;
    private final ServiceProperties serviceProperties;

    @PostConstruct
    public void init() {
        serviceProperties.jobs().forEach(job -> {
            var cron = new CronTrigger(job.cron(), ZoneId.of("America/New_York"));
            refreshTaskScheduler.schedule(() -> closingPricesService.refresh(job), cron);
        });
    }

}
