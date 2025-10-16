--Department Insert
INSERT INTO department (name) VALUES ('HR');
INSERT INTO department (name) VALUES ('IT');

-- Designation Insert
INSERT INTO designation (name, parent_designation_id) VALUES ('CEO', NULL);
INSERT INTO designation (name, parent_designation_id) VALUES ('DEPT_HEAD', 1);
INSERT INTO designation (name, parent_designation_id) VALUES ('MANAGER', 2);
INSERT INTO designation (name, parent_designation_id) VALUES ('DEVELOPER', 3);
INSERT INTO designation (name, parent_designation_id) VALUES ('TESTER', 3);
INSERT INTO designation (name, parent_designation_id) VALUES ('INTERN', 3);