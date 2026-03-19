package com.bank.platform.customer_service.service;

import com.bank.platform.customer_service.dto.CustomerRequest;
import com.bank.platform.customer_service.dto.CustomerResponse;
import java.util.List;

public interface ICustomerService {
    CustomerResponse create(CustomerRequest customerRequest);
    List<CustomerResponse> getAll();
    CustomerResponse getById(Long id);
    void delete(Long id);
}
