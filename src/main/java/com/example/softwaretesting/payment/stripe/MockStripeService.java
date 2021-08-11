package com.example.softwaretesting.payment.stripe;

import com.example.softwaretesting.payment.CardPaymentCharge;
import com.example.softwaretesting.payment.CardPaymentCharger;
import com.example.softwaretesting.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false"
)
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency currency, String description) {
        CardPaymentCharge charge = new CardPaymentCharge(true);
        return charge;
    }
}
