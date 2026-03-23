package com.bank.platform.account_service.controller;

import com.bank.platform.account_service.dto.AccountRequest;
import com.bank.platform.account_service.dto.AccountResponse;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountServiceImpl  accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> save(@RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.create(accountRequest));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> findAll() {
        return ResponseEntity.ok(accountService.getAll());
    }
}
