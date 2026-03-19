package com.bank.platform.customer_service.service;

import com.bank.platform.customer_service.dto.CustomerRequest;
import com.bank.platform.customer_service.dto.CustomerResponse;
import com.bank.platform.customer_service.entity.Customer;
import com.bank.platform.customer_service.mapper.CustomerMapper;
import com.bank.platform.customer_service.repository.ICustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private final ICustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse create(CustomerRequest customerRequest) {
        Customer customer = customerMapper.toEntity(customerRequest);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public List<CustomerResponse> getAll() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return customerMapper.toResponse(customer);
    }

    @Override
    public void delete(Long id) {
        customerRepository.deleteById(id);
    }
}
