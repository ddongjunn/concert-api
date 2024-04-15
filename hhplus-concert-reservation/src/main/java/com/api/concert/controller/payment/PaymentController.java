package com.api.concert.controller.payment;

import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @PostMapping("/concert/payment")
    public String concertPayment(){
        return """
                {
                  "status": "SUCCESS",
                  "message": "{concertStartDate} | {concertName} | {seatNo}"
                }
                """;
    }

}
