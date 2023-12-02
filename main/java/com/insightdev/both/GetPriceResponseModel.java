package com.insightdev.both;

import java.util.ArrayList;

public class GetPriceResponseModel {

   private ArrayList<PriceItem> priceItems;

   String currency;

   String currencySymbol;

    public GetPriceResponseModel(ArrayList<PriceItem> priceItems, String currency, String currencySymbol) {
        this.priceItems = priceItems;
        this.currency = currency;
        this.currencySymbol = currencySymbol;
    }

    public ArrayList<PriceItem> getPriceItems() {
        return priceItems;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }
}
