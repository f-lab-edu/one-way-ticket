CREATE TABLE IF NOT EXISTS booking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    reference_code VARCHAR(255),
    booking_email VARCHAR(255),
    flight_id VARCHAR(255),
    payment_key VARCHAR(255),
    status VARCHAR(50),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS passenger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    passport_number VARCHAR(255),
    gender VARCHAR(10),
    seat_number VARCHAR(10),
    date_of_birth DATE,
    booking_id BIGINT,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS flight (
    id VARCHAR(255) PRIMARY KEY,
    flight_number VARCHAR(255),
    amount DECIMAL(19, 2),
    departure_time DATETIME,
    arrival_time DATETIME,
    origin VARCHAR(255),
    destination VARCHAR(255),
    duration_in_minutes INT,
    carrier VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS toss_payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    toss_payment_key VARCHAR(255) NOT NULL UNIQUE,
    toss_order_id VARCHAR(255) NOT NULL,
    currency VARCHAR(10),
    total_amount DECIMAL(19, 2),
    payment_method VARCHAR(50),
    payment_status VARCHAR(50),
    requested_at DATETIME,
    approved_at DATETIME
);

CREATE TABLE IF NOT EXISTS member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS wishlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    flight_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (member_id, flight_id)
);

CREATE TABLE IF NOT EXISTS price_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19, 2) NOT NULL
);
