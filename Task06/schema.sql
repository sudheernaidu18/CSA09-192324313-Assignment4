-- ==============================================================================
-- Campus Event Registration Desktop Application - Database Schema
-- Student Name: Korasika Sudheer | Register No: 192324313
-- Course: Programming in Java (CSA09) - Assignment 4 (CO4 - Task 6)
-- Compatible with MySQL 8.x / 9.x and H2 (MySQL Mode)
-- ==============================================================================

-- Drop tables if existing for clean reset
DROP TABLE IF EXISTS event_registrations;
DROP TABLE IF EXISTS campus_events;

-- 1. Events Master Table
CREATE TABLE campus_events (
    event_id VARCHAR(20) PRIMARY KEY,
    event_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    max_capacity INT NOT NULL DEFAULT 100
);

-- 2. Student Event Registrations Table
-- Critical Invariant: Composite UNIQUE constraint prevents duplicate student registrations for the same event
CREATE TABLE event_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    reg_no VARCHAR(20) NOT NULL,
    student_name VARCHAR(100) NOT NULL,
    department VARCHAR(50) NOT NULL,
    event_id VARCHAR(20) NOT NULL,
    contact_phone VARCHAR(15) NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES campus_events(event_id),
    CONSTRAINT uk_student_event UNIQUE (reg_no, event_id)
);

-- Seed Initial Campus Cultural Events
INSERT INTO campus_events (event_id, event_name, category, max_capacity) VALUES
('EV-101', 'HackSIMATS 2026 - National Hackathon', 'Technical', 150),
('EV-102', 'RhythmScape - Western Solo Dance', 'Cultural', 50),
('EV-103', 'CodeCraft - Algorithmic Debugging', 'Technical', 80),
('EV-104', 'Symphony - Acoustic Music Band', 'Cultural', 40),
('EV-105', 'CineLens - Short Film Contest', 'Media', 60);

-- Seed Sample Verified Registrations
INSERT INTO event_registrations (reg_no, student_name, department, event_id, contact_phone) VALUES
('192324313', 'Korasika Sudheer', 'Computer Science', 'EV-101', '9876543210'),
('192324314', 'Rohan Verma', 'Information Tech', 'EV-101', '9876543211'),
('192324315', 'Ananya Sharma', 'Biomedical Eng', 'EV-102', '9876543212');
