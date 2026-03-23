package com.bank.platform.account_service.service;

import com.bank.platform.account_service.apiclient.CustomerClient;
import com.bank.platform.account_service.dto.AccountRequest;
import com.bank.platform.account_service.dto.AccountResponse;
import com.bank.platform.account_service.dto.CustomerResponse;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.mapper.AccountMapper;
import com.bank.platform.account_service.repository.IAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements IAccountService {

    private final IAccountRepository accountRepository;
    private final AccountMapper  accountMapper;
    private final CustomerClient customerClient;

    @Override
    public AccountResponse create(AccountRequest accountRequest) {

        CustomerResponse customer =
                customerClient.getCustomerById(accountRequest.getCustomerId());

        Account account = accountMapper.toEntity(accountRequest);

        Account saved = accountRepository.save(account);

        return accountMapper.toResponse(saved,customer);
    }

    @Override
    public List<AccountResponse> getAll() {
        return accountRepository.findAll()
                .stream()
                .map(account -> {
                    CustomerResponse customer =
                            customerClient.getCustomerById(account.getCustomerId());

                    return accountMapper.toResponse(account,customer);
                }).toList();
    }
}
