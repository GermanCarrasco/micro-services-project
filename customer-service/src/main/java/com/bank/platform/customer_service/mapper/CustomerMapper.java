package com.bank.platform.customer_service.mapper;

import com.bank.platform.customer_service.dto.CustomerRequest;
import com.bank.platform.customer_service.dto.CustomerResponse;
import com.bank.platform.customer_service.entity.Customer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request){
        return Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .documentNumber(request.getDocumentNumber())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CustomerResponse toResponse(Customer customer){
            return CustomerResponse.builder()
                    .id(customer.getId())
                    .fullName(customer.getFirstName()+" "+customer.getLastName())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .documentNumber(customer.getDocumentNumber())
                    .createdAt(customer.getCreatedAt())
                    .build();
    }
}
