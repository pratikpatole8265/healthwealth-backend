DROP TABLE IF EXISTS patient_diseases;
DROP TABLE IF EXISTS patient_insurances;
DROP TABLE IF EXISTS policy_diseases;
DROP TABLE IF EXISTS scheme_diseases;
DROP TABLE IF EXISTS hospital_services;
DROP TABLE IF EXISTS diseases;
DROP TABLE IF EXISTS insurance_policies;
DROP TABLE IF EXISTS government_schemes;
DROP TABLE IF EXISTS hospitals;
DROP TABLE IF EXISTS patients;

CREATE TABLE patients (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 identity_ref VARCHAR(64) NOT NULL UNIQUE,
 full_name VARCHAR(120) NOT NULL,
 date_of_birth DATE NOT NULL,
 gender VARCHAR(20) NOT NULL,
 city VARCHAR(80) NOT NULL,
 annual_income DECIMAL(12,2) NOT NULL,
 phone VARCHAR(20),
 blood_group VARCHAR(5),
 emergency_contact VARCHAR(20),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT chk_patient_income CHECK (annual_income >= 0)
);

CREATE TABLE diseases (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(100) NOT NULL UNIQUE,
 category VARCHAR(80) NOT NULL,
 description VARCHAR(500)
);

CREATE TABLE patient_diseases (
 patient_id BIGINT NOT NULL,
 disease_id BIGINT NOT NULL,
 diagnosed_date DATE,
 severity VARCHAR(30),
 status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
 PRIMARY KEY (patient_id, disease_id),
 FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
 FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE RESTRICT
);

CREATE TABLE insurance_policies (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 provider_name VARCHAR(120) NOT NULL,
 policy_name VARCHAR(150) NOT NULL,
 max_coverage DECIMAL(12,2) NOT NULL,
 coverage_percent DECIMAL(5,2) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 CHECK (max_coverage >= 0),
 CHECK (coverage_percent BETWEEN 0 AND 100)
);

CREATE TABLE patient_insurances (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 patient_id BIGINT NOT NULL,
 policy_id BIGINT NOT NULL,
 member_number VARCHAR(80) NOT NULL UNIQUE,
 coverage_remaining DECIMAL(12,2) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
 FOREIGN KEY (policy_id) REFERENCES insurance_policies(id) ON DELETE RESTRICT,
 CHECK (coverage_remaining >= 0)
);

CREATE TABLE policy_diseases (
 policy_id BIGINT NOT NULL,
 disease_id BIGINT NOT NULL,
 PRIMARY KEY (policy_id, disease_id),
 FOREIGN KEY (policy_id) REFERENCES insurance_policies(id) ON DELETE CASCADE,
 FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE CASCADE
);

CREATE TABLE government_schemes (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(150) NOT NULL UNIQUE,
 description VARCHAR(500),
 state_name VARCHAR(80),
 min_age INT,
 max_age INT,
 max_annual_income DECIMAL(12,2),
 max_coverage DECIMAL(12,2) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 CHECK (min_age IS NULL OR min_age >= 0),
 CHECK (max_age IS NULL OR max_age >= 0),
 CHECK (max_annual_income IS NULL OR max_annual_income >= 0),
 CHECK (max_coverage >= 0)
);

CREATE TABLE scheme_diseases (
 scheme_id BIGINT NOT NULL,
 disease_id BIGINT NOT NULL,
 PRIMARY KEY (scheme_id, disease_id),
 FOREIGN KEY (scheme_id) REFERENCES government_schemes(id) ON DELETE CASCADE,
 FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE CASCADE
);

CREATE TABLE hospitals (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 name VARCHAR(160) NOT NULL,
 city VARCHAR(80) NOT NULL,
 address VARCHAR(255) NOT NULL,
 phone VARCHAR(20),
 rating DECIMAL(3,2) NOT NULL DEFAULT 0,
 emergency_available BOOLEAN NOT NULL DEFAULT FALSE,
 CHECK (rating BETWEEN 0 AND 5)
);

CREATE TABLE hospital_services (
 id BIGINT PRIMARY KEY AUTO_INCREMENT,
 hospital_id BIGINT NOT NULL,
 disease_id BIGINT NOT NULL,
 service_name VARCHAR(150) NOT NULL,
 available_slots INT NOT NULL DEFAULT 0,
 estimated_cost DECIMAL(12,2) NOT NULL,
 FOREIGN KEY (hospital_id) REFERENCES hospitals(id) ON DELETE CASCADE,
 FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE CASCADE,
 CHECK (available_slots >= 0),
 CHECK (estimated_cost >= 0),
 UNIQUE (hospital_id, disease_id, service_name)
);

CREATE INDEX idx_patient_city ON patients(city);
CREATE INDEX idx_hospital_city ON hospitals(city);
CREATE INDEX idx_hospital_service_disease ON hospital_services(disease_id);
CREATE INDEX idx_patient_disease_disease ON patient_diseases(disease_id);
