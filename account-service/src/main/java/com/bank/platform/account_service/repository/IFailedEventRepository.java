package com.bank.platform.account_service.repository;

import com.bank.platform.account_service.entity.FailedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFailedEventRepository extends JpaRepository<FailedEvent, Long> {
}
