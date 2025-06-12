-- Create Database
CREATE DATABASE IF NOT EXISTS smart_bee_house CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_bee_house;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'beekeeper') DEFAULT 'beekeeper',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Beehives Table
CREATE TABLE IF NOT EXISTS beehives (
    id INT AUTO_INCREMENT PRIMARY KEY,
    hive_name VARCHAR(50) NOT NULL,
    location VARCHAR(100),
    status ENUM('active', 'inactive') DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Sensors Table
CREATE TABLE IF NOT EXISTS sensors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    beehive_id INT NOT NULL,
    sensor_type ENUM('temperature', 'humidity', 'movement'),
    value FLOAT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE
);

-- Honey Production Table
CREATE TABLE IF NOT EXISTS honey_production (
    id INT AUTO_INCREMENT PRIMARY KEY,
    beehive_id INT NOT NULL,
    quantity FLOAT NOT NULL,
    production_date DATE NOT NULL,
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE
);

-- Alerts Table
CREATE TABLE IF NOT EXISTS alerts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    beehive_id INT NOT NULL,
    alert_type ENUM('temperature_high', 'humidity_low', 'bee_movement_alert'),
    message TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE
);

-- System Logs Table
CREATE TABLE IF NOT EXISTS system_logs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    log_message TEXT NOT NULL,
    log_level ENUM('INFO', 'WARNING', 'ERROR') DEFAULT 'INFO',
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Backup History Table
CREATE TABLE IF NOT EXISTS backup_history (
    id INT AUTO_INCREMENT PRIMARY KEY,
    backup_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    backup_file_path VARCHAR(255) NOT NULL
);