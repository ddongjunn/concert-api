package com.api.concert.controller.payment;

import com.api.concert.application.PaymentFacade;
import com.api.concert.controller.payment.dto.PaymentRequest;
import com.api.concert.controller.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @PostMapping("/concert/payment")
    public PaymentResponse concertPayment(@RequestBody PaymentRequest paymentRequest){
        return paymentFacade.payment(paymentRequest);
    }

}
