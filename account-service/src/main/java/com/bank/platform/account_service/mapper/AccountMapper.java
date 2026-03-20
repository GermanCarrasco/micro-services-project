package com.bank.platform.account_service.mapper;

import com.bank.platform.account_service.dto.AccountRequest;
import com.bank.platform.account_service.dto.AccountResponse;
import com.bank.platform.account_service.dto.CustomerResponse;
import com.bank.platform.account_service.entity.Account;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountMapper {

    public Account toEntity(AccountRequest accountRequest) {
        return Account.builder()
                .customerId(accountRequest.getCustomerId())
                .balance(accountRequest.getInitialBalance())
                .accountNumber("ACC-"+System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public AccountResponse toResponse(Account account, CustomerResponse  customer) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .customer(customer)
                .build();
    }
}
