package com.bank.platform.transaction_service.controller;

import com.bank.platform.transaction_service.dto.TransactionRequest;
import com.bank.platform.transaction_service.dto.TransactionResponse;
import com.bank.platform.transaction_service.dto.TransferRequest;
import com.bank.platform.transaction_service.service.ITransactionService;
import com.bank.platform.transaction_service.service.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {


    private final TransactionServiceImpl transactionService;

    public TransactionController(TransactionServiceImpl transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@RequestBody TransactionRequest transactionRequest) {
        return ResponseEntity.ok(transactionService.createTransaction(transactionRequest));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getByAccount(accountId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request){
        transactionService.transfer(request);
        return ResponseEntity.ok("transfer initiated");
    }
}
