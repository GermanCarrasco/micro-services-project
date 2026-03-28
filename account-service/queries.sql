CREATE TABLE processed_events (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  event_id VARCHAR(100) UNIQUE,
                                  processed_at TIMESTAMP
);