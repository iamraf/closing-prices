package io.iamraf.closingprices.fixture;

import io.iamraf.closingprices.model.ClosingPrice;

public class ClosingPriceFixture {

    public static ClosingPrice ofBtc() {
        return ClosingPrice.of(
                "BTCUSDT",
                BinanceFixture.ofBtc()
        );
    }

}
