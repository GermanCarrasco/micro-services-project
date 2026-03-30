package com.bank.platform.transaction_service.repository;

import com.bank.platform.transaction_service.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Repository
@EnableTransactionManagement
public interface IOutboxRepository extends JpaRepository<OutboxEvent,Long> {
    List<OutboxEvent> findTop10ByStatus(String status);

    @Modifying
    @Query("UPDATE OutboxEvent o SET o.status = 'PROCESSING' WHERE o.id = :id AND o.status = 'PENDING'")
    int markAsProcessing(@Param("id") Long id);

    List<OutboxEvent> findByStatus(String status);
}
