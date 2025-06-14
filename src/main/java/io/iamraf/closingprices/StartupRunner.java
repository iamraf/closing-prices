package io.iamraf.closingprices;

import io.iamraf.closingprices.configuration.properties.ServiceProperties;
import io.iamraf.closingprices.service.ClosingPricesService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements ApplicationRunner {

    private final ServiceProperties properties;
    private final ClosingPricesService service;

    @Override
    public void run(ApplicationArguments args) {
        properties.jobs().forEach(service::refresh);
    }

}
