INSERT INTO patients(identity_ref,full_name,date_of_birth,gender,city,annual_income,phone,blood_group,emergency_contact) VALUES
('DEMO-IDENTITY-1001','Rahul Patil','1974-05-12','MALE','Pune',350000,'9000000001','B+','9111111111'),
('DEMO-IDENTITY-1002','Sneha Joshi','1996-08-24','FEMALE','Mumbai',600000,'9000000002','O+','9222222222'),
('DEMO-IDENTITY-1003','Amit Deshmukh','1959-02-17','MALE','Pune',180000,'9000000003','A+','9333333333');

INSERT INTO diseases(name,category,description) VALUES
('Diabetes','Metabolic','Synthetic demo chronic condition.'),
('Hypertension','Cardiovascular','Synthetic demo high blood pressure condition.'),
('Heart Disease','Cardiovascular','Synthetic demo cardiac condition.'),
('Asthma','Respiratory','Synthetic demo respiratory condition.'),
('Kidney Disease','Renal','Synthetic demo renal condition.');

INSERT INTO patient_diseases(patient_id,disease_id,diagnosed_date,severity,status) VALUES
(1,1,'2020-06-10','MODERATE','ACTIVE'),(1,2,'2021-01-05','MODERATE','ACTIVE'),
(2,4,'2019-03-18','MILD','ACTIVE'),(3,1,'2017-04-11','MODERATE','ACTIVE'),(3,3,'2022-09-01','HIGH','ACTIVE');

INSERT INTO insurance_policies(provider_name,policy_name,max_coverage,coverage_percent,active) VALUES
('DemoCare Insurance','Family Health Protect',500000,80,TRUE),
('SafeHealth Insurance','Senior Care Plus',800000,90,TRUE);

INSERT INTO patient_insurances(patient_id,policy_id,member_number,coverage_remaining,active) VALUES
(1,1,'DEMO-POL-1001',350000,TRUE),(3,2,'DEMO-POL-1003',500000,TRUE);

INSERT INTO policy_diseases(policy_id,disease_id) VALUES
(1,1),(1,2),(1,4),(2,1),(2,3),(2,5);

INSERT INTO government_schemes(name,description,state_name,min_age,max_age,max_annual_income,max_coverage,active) VALUES
('Demo Low Income Health Support','Synthetic demo scheme for low-income patients.','Maharashtra',NULL,NULL,250000,250000,TRUE),
('Demo Senior Care Support','Synthetic demo scheme for eligible senior citizens.','Maharashtra',60,NULL,500000,300000,TRUE),
('Demo Chronic Care Assistance','Synthetic demo scheme for selected chronic diseases.','Maharashtra',NULL,NULL,400000,200000,TRUE);

INSERT INTO scheme_diseases(scheme_id,disease_id) VALUES
(1,1),(1,2),(1,3),(1,4),(1,5),(2,1),(2,3),(2,5),(3,1),(3,2),(3,3);

INSERT INTO hospitals(name,city,address,phone,rating,emergency_available) VALUES
('Pune Care Multispeciality','Pune','Demo Road, Pune','020-40000001',4.6,TRUE),
('City Heart & General Hospital','Pune','Model Colony, Pune','020-40000002',4.3,TRUE),
('Mumbai Wellness Hospital','Mumbai','Andheri Demo Road, Mumbai','022-40000003',4.7,TRUE),
('Community Health Center Pune','Pune','Central Pune','020-40000004',4.0,FALSE);

INSERT INTO hospital_services(hospital_id,disease_id,service_name,available_slots,estimated_cost) VALUES
(1,1,'Diabetes Consultation & Monitoring',8,1200),
(1,2,'Hypertension Consultation',10,1000),
(1,3,'Cardiology Evaluation',4,7500),
(2,1,'Diabetes Consultation & Monitoring',6,900),
(2,2,'Hypertension Consultation',5,1100),
(2,3,'Cardiology Evaluation',7,6500),
(3,4,'Pulmonology Consultation',12,1800),
(4,1,'Diabetes Follow-up',15,500),
(4,2,'Hypertension Follow-up',10,450),
(4,3,'Basic Cardiac Screening',2,2500);
