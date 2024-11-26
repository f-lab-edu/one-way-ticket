INSERT INTO flight (flight_number, amount, departure_time, arrival_time, origin, destination, duration_in_minutes, carrier) VALUES
('AA101', 150.00, '2024-12-01 08:00:00', '2024-12-01 11:00:00', 'ICN', 'LAX', 180, 'American Airlines'),
('UA202', 200.00, '2024-12-01 09:00:00', '2024-12-01 13:00:00', 'ICN', 'ORD', 240, 'United Airlines'),
('DL303', 175.50, '2024-12-02 14:00:00', '2024-12-02 18:00:00', 'ICN', 'SEA', 240, 'Delta Airlines'),
('SW404', 100.75, '2024-12-03 06:00:00', '2024-12-03 08:30:00', 'ICN', 'HOU', 150, 'Southwest Airlines'),
('BA505', 300.00, '2024-12-04 19:00:00', '2024-12-05 07:00:00', 'ICN', 'JFK', 600, 'British Airways'),
('LH606', 400.00, '2024-12-05 20:00:00', '2024-12-06 10:00:00', 'ICN', 'NRT', 840, 'Lufthansa'),
('AF707', 250.00, '2024-12-06 22:00:00', '2024-12-07 12:00:00', 'ICN', 'SIN', 720, 'Air France'),
('QF808', 350.00, '2024-12-07 11:00:00', '2024-12-07 23:00:00', 'ICN', 'DXB', 720, 'Qantas'),
('EK909', 500.00, '2024-12-08 15:00:00', '2024-12-09 05:00:00', 'ICN', 'JFK', 840, 'Emirates'),
('SQ1010', 450.00, '2024-12-09 16:00:00', '2024-12-10 08:00:00', 'ICN', 'SFO', 960, 'Singapore Airlines'),

('AA102', 140.00, '2024-12-01 10:00:00', '2024-12-01 13:00:00', 'ICN', 'LAX', 180, 'American Airlines'),
('UA203', 210.00, '2024-12-01 11:00:00', '2024-12-01 15:00:00', 'ICN', 'ORD', 240, 'United Airlines'),
('DL304', 165.00, '2024-12-02 12:00:00', '2024-12-02 16:30:00', 'ICN', 'SEA', 270, 'Delta Airlines'),
('SW405', 120.00, '2024-12-03 07:00:00', '2024-12-03 10:00:00', 'ICN', 'HOU', 180, 'Southwest Airlines'),
('BA506', 280.00, '2024-12-04 20:00:00', '2024-12-05 08:30:00', 'ICN', 'JFK', 630, 'British Airways'),
('LH607', 390.00, '2024-12-05 22:00:00', '2024-12-06 12:00:00', 'ICN', 'NRT', 840, 'Lufthansa'),
('AF708', 260.00, '2024-12-07 00:00:00', '2024-12-07 14:00:00', 'ICN', 'SIN', 840, 'Air France'),
('QF809', 340.00, '2024-12-07 13:00:00', '2024-12-07 23:59:00', 'ICN', 'DXB', 659, 'Qantas'),
('EK910', 480.00, '2024-12-09 17:00:00', '2024-12-10 06:00:00', 'ICN', 'JFK', 780, 'Emirates'),
('SQ1011', 470.00, '2024-12-10 18:00:00', '2024-12-11 09:30:00', 'ICN', 'SFO', 930, 'Singapore Airlines');

INSERT INTO Booking (reference_code, booking_email, flight_id, payment_id, status, created_at)
VALUES
('B1234', 'johndoe@example.com', 123, 456, 'CONFIRMED', '2023-11-25 14:30:00'),
('B1235', 'alice@example.com', 124, 457, 'CONFIRMED', '2023-11-26 09:00:00'),
('B1236', 'bob@example.com', 125, 458, 'PENDING', '2023-11-27 13:45:00'),
('B1237', 'charlie@example.com', 126, 459, 'CANCELLED', '2023-11-28 16:20:00'),
('B1238', 'eve@example.com', 127, 460, 'CONFIRMED', '2023-11-29 10:10:00');

INSERT INTO Passenger (reference_code, first_name, last_name, passport_number, gender, seat_number, date_of_birth, booking_id)
VALUES
('P1234', 'John', 'Doe', 'A12345678', 'Male', '12A', '1995-05-26', 1),
('P1235', 'Jane', 'Doe', 'B98765432', 'Female', '12B', '1998-03-14', 1),
('P1236', 'Alice', 'Smith', 'C87654321', 'Female', '14A', '1992-08-15', 2),
('P1237', 'Bob', 'Smith', 'C98765432', 'Male', '14B', '1990-06-21', 2),
('P1238', 'Bob', 'Brown', 'D12345678', 'Male', '15C', '1985-03-12', 3),
('P1239', 'Charlie', 'Johnson', 'E12345678', 'Male', '16A', '1993-01-10', 4),
('P1240', 'Emily', 'Johnson', 'E87654321', 'Female', '16B', '1994-12-05', 4),
('P1241', 'Eve', 'Davis', 'F12345678', 'Female', '17A', '1988-11-22', 5),
('P1242', 'Oscar', 'Davis', 'F87654321', 'Male', '17B', '1987-04-14', 5);
