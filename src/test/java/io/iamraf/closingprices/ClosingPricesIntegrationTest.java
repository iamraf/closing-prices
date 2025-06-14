package io.iamraf.closingprices;

import io.iamraf.closingprices.configuration.client.BinanceRestClient;
import io.iamraf.closingprices.fixture.BinanceFixture;
import io.iamraf.closingprices.service.ClosingPricesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ClosingPricesIntegrationTest {

    @Autowired
    private ClosingPricesService service;

    @Test
    public void testValidate12PMEntries() {
        var entries = service.getLatestPrices("12");

        assertEquals(2, entries.size());

        assertEquals("BTCUSDT", entries.getFirst().symbol());
        assertEquals(BigDecimal.valueOf(103000.0), entries.getFirst().close());

        assertEquals("ETHUSDT", entries.getLast().symbol());
        assertEquals(BigDecimal.valueOf(2550.0), entries.getLast().close());
    }

    @Test
    public void testValidate17PMEntries() {
        var entries = service.getLatestPrices("17");

        assertEquals(2, entries.size());

        assertEquals("SOLUSDT", entries.getFirst().symbol());
        assertEquals(BigDecimal.valueOf(145.0), entries.getFirst().close());

        assertEquals("XRPUSDT", entries.getLast().symbol());
        assertEquals(BigDecimal.valueOf(2.20), entries.getLast().close());
    }

    @Test
    public void testInvalidEntry() {
        var entries = service.getLatestPrices("1");

        assertTrue(entries.isEmpty());
    }

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public BinanceRestClient restClient() {
            var mock = mock(BinanceRestClient.class);

            when(mock.getPrices(eq("BTCUSDT"), anyLong())).thenReturn(List.of(BinanceFixture.ofBtc()));
            when(mock.getPrices(eq("ETHUSDT"), anyLong())).thenReturn(List.of(BinanceFixture.ofEth()));
            when(mock.getPrices(eq("SOLUSDT"), anyLong())).thenReturn(List.of(BinanceFixture.ofSol()));
            when(mock.getPrices(eq("XRPUSDT"), anyLong())).thenReturn(List.of(BinanceFixture.ofXrp()));

            return mock;
        }
    }

}
