package io.iamraf.closingprices.controller;

import io.iamraf.closingprices.fixture.ClosingPriceFixture;
import io.iamraf.closingprices.service.ClosingPricesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClosingPricesController.class)
public class ClosingPricesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClosingPricesService service;

    @Test
    public void testValidTime() throws Exception {
        when(service.getLatestPrices("12")).thenReturn(List.of(ClosingPriceFixture.ofBtc()));

        mockMvc.perform(get("/prices?time=12"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].symbol").value("BTCUSDT"))
                .andExpect(jsonPath("$.[0].close").value(BigDecimal.valueOf(103000.0)));
    }

    @Test
    public void testInvalidTime() throws Exception {
        when(service.getLatestPrices("1")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/prices?time=1"))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

}
