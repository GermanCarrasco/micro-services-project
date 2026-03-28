package com.bank.platform.account_service.repository;

import com.bank.platform.account_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProcessedEventRepository extends JpaRepository<ProcessedEvent,Long> {

    boolean existsByEventId(String eventId);
}
