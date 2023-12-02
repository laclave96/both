package com.insightdev.both;

public class PaymentModel {

    private final String paymentMethodId;
    private final String idempotencyKey;
    private final String amount;
    private final String name;
    private final String currency;
    private final int planDuration;


    public PaymentModel(String paymentMethodId, String idempotencyKey, String amount, String name, String currency, int planDuration) {
        this.paymentMethodId = paymentMethodId;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
        this.name = name;
        this.currency = currency;
        this.planDuration = planDuration;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() {
        return currency;
    }

    public int getPlanDuration() {
        return planDuration;
    }
}
