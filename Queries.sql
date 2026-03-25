--Tabla Outbox

CREATE TABLE outbox (
                        id BIGINT AUTO_INCREMENT,
                        event_type VARCHAR(50),
                        payload TEXT,
                        status VARCHAR(20),
                        created_at TIMESTAMP
);