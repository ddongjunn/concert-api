package com.api.concert.controller.payment;

import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    @PatchMapping("/point")
    public String patchPoint(){
        return """
                {
                  "status": "SUCCESS",
                  "charge_point": 1000,
                  "point": 5000
                }
                """;
    }

    @GetMapping("/point/{userId}")
    public String getPoint(@PathVariable Long userId){
        return """
                {
                  "status": "SUCCESS",
                  "point": 2000
                }
                """;
    }

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
