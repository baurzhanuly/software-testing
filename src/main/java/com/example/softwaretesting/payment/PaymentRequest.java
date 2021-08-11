package com.example.softwaretesting.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentRequest {

    private final Payment payment;

    public PaymentRequest(@JsonProperty("payment") Payment payment) {
        this.payment = payment;
    }

    public Payment getPayment() {
        return payment;
    }
}
