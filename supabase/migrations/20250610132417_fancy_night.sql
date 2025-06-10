-- Smart Bee House Management System - Database Schema
-- Base de données MySQL pour le système de gestion apiculture

-- Création de la base de données
CREATE DATABASE IF NOT EXISTS smart_bee_house 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE smart_bee_house;

-- Table des fermiers/propriétaires
CREATE TABLE farmers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    INDEX idx_farmers_email (email),
    INDEX idx_farmers_active (active),
    INDEX idx_farmers_name (last_name, first_name)
);

-- Table des fermes
CREATE TABLE farms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    address TEXT,
    owner_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (owner_id) REFERENCES farmers(id) ON DELETE CASCADE,
    INDEX idx_farms_owner (owner_id),
    INDEX idx_farms_active (active),
    INDEX idx_farms_name (name)
);

-- Table des sites d'apiculture
CREATE TABLE apiary_sites (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    altitude DECIMAL(8, 2),
    implementation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    relocation_date TIMESTAMP NULL,
    closure_date TIMESTAMP NULL,
    farm_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (farm_id) REFERENCES farms(id) ON DELETE CASCADE,
    INDEX idx_sites_farm (farm_id),
    INDEX idx_sites_location (latitude, longitude),
    INDEX idx_sites_active (active),
    INDEX idx_sites_name (name)
);

-- Table des agents apiculteurs
CREATE TABLE agents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20),
    role ENUM('TECHNICIAN', 'SUPERVISOR', 'MANAGER', 'SPECIALIST') DEFAULT 'TECHNICIAN',
    specialization VARCHAR(200),
    supervisor_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (supervisor_id) REFERENCES agents(id) ON DELETE SET NULL,
    INDEX idx_agents_email (email),
    INDEX idx_agents_supervisor (supervisor_id),
    INDEX idx_agents_role (role),
    INDEX idx_agents_active (active),
    INDEX idx_agents_name (last_name, first_name)
);

-- Table des ruches
CREATE TABLE beehives (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    site_id BIGINT NOT NULL,
    current_agent_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (site_id) REFERENCES apiary_sites(id) ON DELETE CASCADE,
    FOREIGN KEY (current_agent_id) REFERENCES agents(id) ON DELETE SET NULL,
    INDEX idx_beehives_site (site_id),
    INDEX idx_beehives_agent (current_agent_id),
    INDEX idx_beehives_active (active),
    INDEX idx_beehives_name (name)
);

-- Table des socles/corps de ruches
CREATE TABLE beehive_bases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) DEFAULT 'Base',
    beehive_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE,
    INDEX idx_bases_beehive (beehive_id)
);

-- Table des extensions/hausses
CREATE TABLE beehive_extensions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level INT NOT NULL CHECK (level BETWEEN 1 AND 5),
    beehive_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE,
    INDEX idx_extensions_beehive (beehive_id),
    INDEX idx_extensions_level (level),
    UNIQUE KEY uk_beehive_level (beehive_id, level)
);

-- Table des cadres
CREATE TABLE frames (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slot INT NOT NULL CHECK (slot BETWEEN 1 AND 10),
    type ENUM('WAX', 'PLASTIC', 'WOOD') DEFAULT 'WAX',
    state ENUM('EMPTY', 'PARTIAL', 'FULL', 'DAMAGED') DEFAULT 'EMPTY',
    honey_weight DECIMAL(8, 2) DEFAULT 0.00,
    container_type ENUM('BASE', 'EXTENSION') NOT NULL,
    container_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_frames_container (container_type, container_id),
    INDEX idx_frames_slot (slot),
    INDEX idx_frames_state (state),
    UNIQUE KEY uk_container_slot (container_type, container_id, slot)
);

-- Table des visites
CREATE TABLE visits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    beehive_id BIGINT NOT NULL,
    agent_id BIGINT NOT NULL,
    approved_by_id BIGINT NULL,
    scheduled_date TIMESTAMP NOT NULL,
    actual_date TIMESTAMP NULL,
    duration INT NULL COMMENT 'Durée en minutes',
    reason ENUM(
        'HEALTH_INSPECTION', 'NEW_SWARM', 'QUEEN_CHANGE', 'FEEDING',
        'MEDICATION', 'SWARM_DIVISION', 'FRAME_ADDITION', 'EXTENSION_ADDITION',
        'HONEY_COLLECTION', 'PRODUCTION_EVALUATION', 'POPULATION_EVALUATION', 'ROUTINE_CHECK'
    ) NOT NULL,
    status ENUM('PLANNED', 'APPROVED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'POSTPONED') DEFAULT 'PLANNED',
    observations TEXT,
    planned_actions TEXT,
    performed_actions TEXT,
    recommendations TEXT,
    swarm_population_rating INT CHECK (swarm_population_rating BETWEEN 1 AND 3),
    health_rating INT CHECK (health_rating BETWEEN 1 AND 3),
    productivity_rating INT CHECK (productivity_rating BETWEEN 1 AND 3),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by_id) REFERENCES agents(id) ON DELETE SET NULL,
    INDEX idx_visits_beehive (beehive_id),
    INDEX idx_visits_agent (agent_id),
    INDEX idx_visits_scheduled (scheduled_date),
    INDEX idx_visits_status (status),
    INDEX idx_visits_reason (reason)
);

-- Table des mesures de capteurs
CREATE TABLE sensor_measurements (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    beehive_id BIGINT NOT NULL,
    indicator_type ENUM(
        'WEIGHT', 'TEMPERATURE_INSIDE', 'TEMPERATURE_OUTSIDE',
        'HUMIDITY_INSIDE', 'HUMIDITY_OUTSIDE', 'BEE_MOVEMENT_IN', 'BEE_MOVEMENT_OUT',
        'SOUND_INSIDE', 'SOUND_OUTSIDE', 'LIGHT_INSIDE', 'LIGHT_OUTSIDE',
        'WIND_SPEED', 'WIND_DIRECTION', 'HIVE_OPENED'
    ) NOT NULL,
    value DECIMAL(10, 4) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sensor_id VARCHAR(100),
    location ENUM('INSIDE', 'OUTSIDE', 'BASE', 'EXTENSION', 'FRAME'),
    
    FOREIGN KEY (beehive_id) REFERENCES beehives(id) ON DELETE CASCADE,
    INDEX idx_measurements_beehive (beehive_id),
    INDEX idx_measurements_type (indicator_type),
    INDEX idx_measurements_timestamp (timestamp),
    INDEX idx_measurements_sensor (sensor_id)
);

-- Table des utilisateurs pour l'authentification
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    google_id VARCHAR(255) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    profile_picture_url TEXT,
    role ENUM('ADMIN', 'MANAGER', 'AGENT', 'FARMER') DEFAULT 'FARMER',
    farmer_id BIGINT NULL,
    agent_id BIGINT NULL,
    last_login TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    
    FOREIGN KEY (farmer_id) REFERENCES farmers(id) ON DELETE SET NULL,
    FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE SET NULL,
    INDEX idx_users_email (email),
    INDEX idx_users_google_id (google_id),
    INDEX idx_users_role (role),
    INDEX idx_users_active (active)
);

-- Table des sessions utilisateur
CREATE TABLE user_sessions (
    id VARCHAR(255) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_sessions_user (user_id),
    INDEX idx_sessions_expires (expires_at)
);

-- Table des paramètres système
CREATE TABLE system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    description TEXT,
    category VARCHAR(50),
    data_type ENUM('STRING', 'INTEGER', 'DECIMAL', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_settings_key (setting_key),
    INDEX idx_settings_category (category)
);

-- Table des logs d'audit
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(100),
    record_id BIGINT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_table (table_name),
    INDEX idx_audit_created (created_at)
);

-- Insertion des paramètres par défaut
INSERT INTO system_settings (setting_key, setting_value, description, category, data_type) VALUES
('honey.production.high.threshold', '5000.0', 'Seuil de production élevée en grammes', 'THRESHOLDS', 'DECIMAL'),
('honey.production.low.threshold', '1000.0', 'Seuil de production faible en grammes', 'THRESHOLDS', 'DECIMAL'),
('temperature.min.threshold', '10.0', 'Température minimale en Celsius', 'THRESHOLDS', 'DECIMAL'),
('temperature.max.threshold', '50.0', 'Température maximale en Celsius', 'THRESHOLDS', 'DECIMAL'),
('humidity.min.threshold', '20.0', 'Humidité minimale en pourcentage', 'THRESHOLDS', 'DECIMAL'),
('humidity.max.threshold', '90.0', 'Humidité maximale en pourcentage', 'THRESHOLDS', 'DECIMAL'),
('default.language', 'fr', 'Langue par défaut du système', 'SYSTEM', 'STRING'),
('session.timeout', '1800', 'Timeout de session en secondes', 'SYSTEM', 'INTEGER'),
('max.beehives.per.site', '50', 'Nombre maximum de ruches par site', 'LIMITS', 'INTEGER'),
('max.extensions.per.beehive', '5', 'Nombre maximum d\'extensions par ruche', 'LIMITS', 'INTEGER'),
('max.frames.per.container', '10', 'Nombre maximum de cadres par conteneur', 'LIMITS', 'INTEGER');

-- Création des vues pour faciliter les requêtes

-- Vue des ruches avec informations complètes
CREATE VIEW v_beehives_full AS
SELECT 
    b.id,
    b.name,
    b.description,
    b.active,
    b.created_at,
    b.updated_at,
    s.name AS site_name,
    s.latitude,
    s.longitude,
    s.altitude,
    f.name AS farm_name,
    farmer.first_name AS farmer_first_name,
    farmer.last_name AS farmer_last_name,
    agent.first_name AS agent_first_name,
    agent.last_name AS agent_last_name,
    agent.email AS agent_email,
    COALESCE(MAX(sm.value), 0) AS current_honey_quantity
FROM beehives b
LEFT JOIN apiary_sites s ON b.site_id = s.id
LEFT JOIN farms f ON s.farm_id = f.id
LEFT JOIN farmers farmer ON f.owner_id = farmer.id
LEFT JOIN agents agent ON b.current_agent_id = agent.id
LEFT JOIN sensor_measurements sm ON b.id = sm.beehive_id AND sm.indicator_type = 'WEIGHT'
GROUP BY b.id, b.name, b.description, b.active, b.created_at, b.updated_at,
         s.name, s.latitude, s.longitude, s.altitude, f.name,
         farmer.first_name, farmer.last_name,
         agent.first_name, agent.last_name, agent.email;

-- Vue des visites avec informations complètes
CREATE VIEW v_visits_full AS
SELECT 
    v.id,
    v.scheduled_date,
    v.actual_date,
    v.duration,
    v.reason,
    v.status,
    v.observations,
    v.planned_actions,
    v.performed_actions,
    v.recommendations,
    v.swarm_population_rating,
    v.health_rating,
    v.productivity_rating,
    v.created_at,
    v.updated_at,
    b.name AS beehive_name,
    s.name AS site_name,
    agent.first_name AS agent_first_name,
    agent.last_name AS agent_last_name,
    supervisor.first_name AS supervisor_first_name,
    supervisor.last_name AS supervisor_last_name
FROM visits v
JOIN beehives b ON v.beehive_id = b.id
JOIN apiary_sites s ON b.site_id = s.id
JOIN agents agent ON v.agent_id = agent.id
LEFT JOIN agents supervisor ON v.approved_by_id = supervisor.id;

-- Vue des statistiques de production par site
CREATE VIEW v_production_stats_by_site AS
SELECT 
    s.id AS site_id,
    s.name AS site_name,
    f.name AS farm_name,
    COUNT(b.id) AS total_beehives,
    COUNT(CASE WHEN b.active = TRUE THEN 1 END) AS active_beehives,
    COALESCE(SUM(sm.value), 0) AS total_honey_production,
    COALESCE(AVG(sm.value), 0) AS average_honey_per_beehive,
    COUNT(v.id) AS total_visits,
    COUNT(CASE WHEN v.status = 'COMPLETED' THEN 1 END) AS completed_visits
FROM apiary_sites s
LEFT JOIN farms f ON s.farm_id = f.id
LEFT JOIN beehives b ON s.id = b.site_id
LEFT JOIN sensor_measurements sm ON b.id = sm.beehive_id AND sm.indicator_type = 'WEIGHT'
LEFT JOIN visits v ON b.id = v.beehive_id
GROUP BY s.id, s.name, f.name;

-- Procédures stockées

DELIMITER //

-- Procédure pour calculer la quantité de miel d'une ruche
CREATE PROCEDURE GetBeehiveHoneyQuantity(IN beehive_id BIGINT, OUT honey_quantity DECIMAL(10,2))
BEGIN
    SELECT COALESCE(MAX(value), 0) INTO honey_quantity
    FROM sensor_measurements 
    WHERE beehive_id = beehive_id AND indicator_type = 'WEIGHT';
END //

-- Procédure pour obtenir les ruches nécessitant une attention
CREATE PROCEDURE GetBeehivesNeedingAttention(IN production_threshold DECIMAL(10,2))
BEGIN
    SELECT b.*, 
           COALESCE(MAX(sm.value), 0) AS current_production,
           s.name AS site_name,
           f.name AS farm_name
    FROM beehives b
    LEFT JOIN sensor_measurements sm ON b.id = sm.beehive_id AND sm.indicator_type = 'WEIGHT'
    LEFT JOIN apiary_sites s ON b.site_id = s.id
    LEFT JOIN farms f ON s.farm_id = f.id
    WHERE b.active = TRUE
    GROUP BY b.id
    HAVING current_production < production_threshold
    ORDER BY current_production ASC;
END //

-- Procédure pour obtenir le planning des visites
CREATE PROCEDURE GetVisitSchedule(IN start_date DATE, IN end_date DATE)
BEGIN
    SELECT v.*, 
           b.name AS beehive_name,
           s.name AS site_name,
           CONCAT(a.first_name, ' ', a.last_name) AS agent_name
    FROM visits v
    JOIN beehives b ON v.beehive_id = b.id
    JOIN apiary_sites s ON b.site_id = s.id
    JOIN agents a ON v.agent_id = a.id
    WHERE DATE(v.scheduled_date) BETWEEN start_date AND end_date
    ORDER BY v.scheduled_date ASC;
END //

DELIMITER ;

-- Triggers pour l'audit

DELIMITER //

CREATE TRIGGER tr_farmers_audit_insert
AFTER INSERT ON farmers
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, new_values, created_at)
    VALUES ('INSERT', 'farmers', NEW.id, JSON_OBJECT(
        'first_name', NEW.first_name,
        'last_name', NEW.last_name,
        'email', NEW.email,
        'active', NEW.active
    ), NOW());
END //

CREATE TRIGGER tr_farmers_audit_update
AFTER UPDATE ON farmers
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, old_values, new_values, created_at)
    VALUES ('UPDATE', 'farmers', NEW.id, 
        JSON_OBJECT(
            'first_name', OLD.first_name,
            'last_name', OLD.last_name,
            'email', OLD.email,
            'active', OLD.active
        ),
        JSON_OBJECT(
            'first_name', NEW.first_name,
            'last_name', NEW.last_name,
            'email', NEW.email,
            'active', NEW.active
        ), 
        NOW());
END //

CREATE TRIGGER tr_beehives_audit_insert
AFTER INSERT ON beehives
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, new_values, created_at)
    VALUES ('INSERT', 'beehives', NEW.id, JSON_OBJECT(
        'name', NEW.name,
        'site_id', NEW.site_id,
        'current_agent_id', NEW.current_agent_id,
        'active', NEW.active
    ), NOW());
END //

CREATE TRIGGER tr_beehives_audit_update
AFTER UPDATE ON beehives
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (action, table_name, record_id, old_values, new_values, created_at)
    VALUES ('UPDATE', 'beehives', NEW.id,
        JSON_OBJECT(
            'name', OLD.name,
            'site_id', OLD.site_id,
            'current_agent_id', OLD.current_agent_id,
            'active', OLD.active
        ),
        JSON_OBJECT(
            'name', NEW.name,
            'site_id', NEW.site_id,
            'current_agent_id', NEW.current_agent_id,
            'active', NEW.active
        ),
        NOW());
END //

DELIMITER ;

-- Index pour optimiser les performances
CREATE INDEX idx_sensor_measurements_composite ON sensor_measurements(beehive_id, indicator_type, timestamp DESC);
CREATE INDEX idx_visits_composite ON visits(beehive_id, status, scheduled_date);
CREATE INDEX idx_audit_logs_composite ON audit_logs(table_name, record_id, created_at DESC);

-- Données de test (optionnel)
INSERT INTO farmers (first_name, last_name, email, phone, address) VALUES
('Jean', 'Dupont', 'jean.dupont@email.com', '+33123456789', '123 Rue de la Ruche, 75001 Paris'),
('Marie', 'Martin', 'marie.martin@email.com', '+33987654321', '456 Avenue des Abeilles, 69001 Lyon'),
('Pierre', 'Durand', 'pierre.durand@email.com', '+33555666777', '789 Boulevard du Miel, 13001 Marseille');

INSERT INTO farms (name, description, address, owner_id) VALUES
('Ferme du Soleil', 'Ferme apicole familiale spécialisée dans le miel de lavande', '123 Rue de la Ruche, 75001 Paris', 1),
('Les Ruches d\'Or', 'Production de miel bio et produits dérivés', '456 Avenue des Abeilles, 69001 Lyon', 2),
('Miel de Provence', 'Apiculture traditionnelle en Provence', '789 Boulevard du Miel, 13001 Marseille', 3);

INSERT INTO apiary_sites (name, description, latitude, longitude, altitude, farm_id) VALUES
('Site Lavande Nord', 'Site principal avec 20 ruches', 48.8566, 2.3522, 35.0, 1),
('Site Forêt Sud', 'Site secondaire en bordure de forêt', 45.7640, 4.8357, 200.0, 2),
('Site Colline Est', 'Site sur colline avec vue panoramique', 43.2965, 5.3698, 150.0, 3);

INSERT INTO agents (first_name, last_name, email, phone, role, specialization) VALUES
('Antoine', 'Moreau', 'antoine.moreau@smartbeehouse.com', '+33111222333', 'SUPERVISOR', 'Santé des abeilles'),
('Sophie', 'Bernard', 'sophie.bernard@smartbeehouse.com', '+33444555666', 'TECHNICIAN', 'Production de miel'),
('Lucas', 'Petit', 'lucas.petit@smartbeehouse.com', '+33777888999', 'TECHNICIAN', 'Maintenance des ruches');

-- Mise à jour des relations de supervision
UPDATE agents SET supervisor_id = 1 WHERE id IN (2, 3);