package com.bank.platform.account_service.service;

public interface IOutboxService {

    void saveEvent(Object event,String eventType,String eventId);
}
