package endpoints;

import java.util.Map;

public class pdfFields {

     public static final Map<String, String> SPOT_TRADE_CONFIRMATION = Map.of(
        "Instrument", "SPOT\\s+([A-Z]{3}/[A-Z]{3})",
        "BuyAmount", "buy\\s+(\\d+\\.\\d+)",
        "SellAmount", "sell\\s+(\\d+\\.\\d+)",
        "Rate", "rate\\s+(\\d+\\.\\d+)",
        "counterparty", "counterparty\\s+([A-Za-z]+)"
    );

    public static final Map<String, String> FORWARD_TRADE_CONFIRMATION = Map.of(
        "Instrument", "FORWARD\\s+([A-Z]{3}/[A-Z]{3})",
        "BuyAmount", "buy\\s+(\\d+\\.\\d+)",
        "SellAmount", "sell\\s+(\\d+\\.\\d+)",
        "Rate", "rate\\s+(\\d+\\.\\d+)",
        "ValueDate", "valueDate\\s+(\\d{4}-\\d{2}-\\d{2})",
        "counterparty", "counterparty\\s+([A-Za-z]+)"
    );

    private pdfFields() {
         /* prevent instantiation */ 
        }
}
