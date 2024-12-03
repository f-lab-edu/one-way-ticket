-- Booking 테이블
CREATE TABLE booking ( id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reference_code VARCHAR(255),
    booking_email VARCHAR(255),
    flight_id BIGINT,
    payment_key VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP
);

-- Passenger 테이블
CREATE TABLE passenger (id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    passport_number VARCHAR(255),
    gender VARCHAR(10),
    seat_number VARCHAR(10),
    date_of_birth DATE,
    booking_id BIGINT,
    FOREIGN KEY (booking_id) REFERENCES booking(id) ON DELETE CASCADE
);

-- Flight 테이블
CREATE TABLE flight (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(255),
    amount DECIMAL(19, 2),
    departure_time TIMESTAMP,
    arrival_time TIMESTAMP,
    origin VARCHAR(255),
    destination VARCHAR(255),
    duration_in_minutes INT,
    carrier VARCHAR(255)
);

-- TossPayment 테이블
CREATE TABLE toss_payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    toss_payment_key VARCHAR(255) NOT NULL UNIQUE,
    toss_order_id VARCHAR(255) NOT NULL,
    currency VARCHAR(10),
    total_amount DECIMAL(19, 2),
    payment_method VARCHAR(50),
    payment_status VARCHAR(50),
    requested_at TIMESTAMP,
    approved_at TIMESTAMP
);

-- User 테이블
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);
