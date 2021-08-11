package com.example.softwaretesting.payment;

import com.example.softwaretesting.customer.Customer;
import com.example.softwaretesting.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new PaymentService(paymentRepository,customerRepository,cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId,"Bob", "87007770852");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        Currency currency = Currency.USD;

        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));
        underTest.chargeCard(customerId, paymentRequest);

        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);

        then(paymentRepository).should().save(paymentArgumentCaptor.capture());

        Payment payment = paymentArgumentCaptor.getValue();
        assertThat(payment).isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");
        assertThat(payment.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardNotCharged() {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId,"Bob", "87007770852");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        Currency currency = Currency.USD;

        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        given(cardPaymentCharger.chargeCard(
                paymentRequest.getPayment().getSource(),
                paymentRequest.getPayment().getAmount(),
                paymentRequest.getPayment().getCurrency(),
                paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Card not debited for customer %s", customerId));


        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        //Given
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId,"Bob", "87007770852");
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer));

        Currency currency = Currency.EUR;

        PaymentRequest paymentRequest = new PaymentRequest(
                new Payment(
                        null,
                        null,
                        new BigDecimal("100.00"),
                        currency,
                        "card123xx",
                        "Donation"
                )
        );

        assertThatThrownBy(() -> underTest.chargeCard(customerId,paymentRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Currency[%s] not supported", paymentRequest.getPayment().getCurrency()));

        then(cardPaymentCharger).shouldHaveNoInteractions();


        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrowWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        given(customerRepository.findById(customerId)).willReturn(Optional.empty());


        assertThatThrownBy(() -> underTest.chargeCard(customerId, new PaymentRequest(new Payment())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Customer with id [" + customerId + "] not found");

        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}