CREATE TABLE processed_events (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  event_id VARCHAR(100) UNIQUE,
                                  processed_at TIMESTAMP
);

--------------------------------------------------------------------------

CREATE TABLE failed_events (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               payload TEXT,
                               error_message TEXT,
                               created_at TIMESTAMP
);