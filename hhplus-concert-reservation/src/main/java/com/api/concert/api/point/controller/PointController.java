package com.api.concert.api.point.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {

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
}
