CREATE TABLE match_data (
    id IDENTITY PRIMARY KEY,
    match_id VARCHAR(50) NOT NULL,
    market_id INT NOT NULL,
    outcome_id VARCHAR(100) NOT NULL,
    specifiers VARCHAR(500),
    date_insert TIMESTAMP NOT NULL
);
