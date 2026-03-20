package com.bank.platform.account_service.service;

import com.bank.platform.account_service.dto.AccountRequest;
import com.bank.platform.account_service.dto.AccountResponse;
import java.util.List;


public interface IAccountService {

    public AccountResponse create(AccountRequest accountRequest);
    public List<AccountResponse> findAll();
}
