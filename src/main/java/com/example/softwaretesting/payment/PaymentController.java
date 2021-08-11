package com.example.softwaretesting.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/v1/payment")
public class PaymentController {


    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @RequestMapping
    public void makePayment(@RequestBody PaymentRequest request){
        paymentService.chargeCard(request.getPayment().getCustomerId(),request);
    }
}
