package com.bank.platform.transaction_service.repository;

import com.bank.platform.transaction_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IOutboxRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findByStatus(String status);
}
