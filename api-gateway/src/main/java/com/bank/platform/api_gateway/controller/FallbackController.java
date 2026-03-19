package com.bank.platform.api_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/customers")
    public String customerFallback(){
        return "Customer Service is temporarily unavailable";
    }
}
