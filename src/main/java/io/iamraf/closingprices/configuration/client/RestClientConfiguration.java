package io.iamraf.closingprices.configuration.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfiguration {

    @Bean
    public BinanceRestClient binanceRestClient() {
        var client = RestClient.builder()
                .baseUrl("https://api.binance.com")
                .build();

        var factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build();

        return factory.createClient(BinanceRestClient.class);
    }

}
