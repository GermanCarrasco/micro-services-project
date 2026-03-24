package com.bank.platform.transaction_service.service;

import com.bank.platform.transaction_service.dto.TransactionRequest;
import com.bank.platform.transaction_service.dto.TransactionResponse;
import com.bank.platform.transaction_service.entity.Transaction;

import java.util.*;

public interface ITransactionService {
    TransactionResponse createTransaction(TransactionRequest transactionRequest);
    List<TransactionResponse> getByAccount(Long accountId);
    TransactionResponse mapToResponse(Transaction t);
}
