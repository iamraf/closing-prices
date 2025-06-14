package io.iamraf.closingprices.configuration.properties;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties("service")
@Validated
public record ServiceProperties(
        @NotNull List<Job> jobs
) {

    public record Job(
            @NotNull String id,
            @NotNull String cron,
            @NotNull List<String> symbols) {
    }

}
