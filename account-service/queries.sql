set search_path to account;

CREATE TABLE processed_events (
                                  id  BIGSERIAL PRIMARY KEY,
                                  event_id VARCHAR(100) UNIQUE,
                                  processed_at TIMESTAMP
);


CREATE TABLE failed_events (
                               id BIGSERIAL PRIMARY KEY,
                               payload TEXT,
                               error_message VARCHAR(255),
                               created_at TIMESTAMP,
                               event_id VARCHAR(255),
                               retry_count INTEGER,
                               status VARCHAR(50),
                               last_attempt_at TIMESTAMP
);


CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          account_number VARCHAR(100),
                          balance DOUBLE PRECISION,
                          customer_id BIGINT,
                          created_at TIMESTAMP
);

CREATE TABLE outbox_event (
                              id BIGSERIAL PRIMARY KEY,
                              event_id VARCHAR(255),
                              event_type VARCHAR(255),
                              payload TEXT,
                              status VARCHAR(50),
                              created_at TIMESTAMP
);

