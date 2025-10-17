package com.example.orgapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.orgapp.entity.Department;
import com.example.orgapp.entity.Designation;
import com.example.orgapp.entity.Employee;
import com.example.orgapp.enums.OrgDesignations;
import com.example.orgapp.repository.DepartmentRepository;
import com.example.orgapp.repository.DesignationRepository;
import com.example.orgapp.repository.EmployeeRepository;

@Service
@Transactional
public class OrgService {

	private final DepartmentRepository departmentRepo;
	private final EmployeeRepository employeeRepo;
	private final DesignationRepository desgRepo;

	public OrgService(DepartmentRepository departmentRepo, EmployeeRepository employeeRepo,
			DesignationRepository desgRepo) {
		this.departmentRepo = departmentRepo;
		this.employeeRepo = employeeRepo;
		this.desgRepo = desgRepo;
	}

	// ---------------- Department ----------------
	public Department addDepartment(Department dept) {
		dept.setDepartmentName(dept.getDepartmentName().toUpperCase());
		Optional<Department> existingDept = departmentRepo.findByDepartmentName(dept.getDepartmentName());
		if (existingDept.isPresent()) {
			return null;
		}
		return departmentRepo.save(dept);
	}

	// ---------------- Employee ----------------
	public Employee addEmployee(Employee emp) {

		// Mobile number uniqueness check
		if (emp.getMobileNumber() == null || emp.getMobileNumber().isEmpty()) {
			throw new IllegalArgumentException("Mobile number is required");
		}
		if (employeeRepo.existsByMobileNumber(emp.getMobileNumber())) {
			throw new IllegalArgumentException("Employee with this mobile number already exists");
		}

		// Validate designation using enum
		if (emp.getDesignation() == null || emp.getDesignation().getOrgDesignations() == null) {
			throw new IllegalArgumentException("Designation is required");
		}

		OrgDesignations designation = emp.getDesignation().getOrgDesignations();
		if (!OrgDesignations.isValidOrgDesignation(designation.name())) {
			throw new IllegalArgumentException("Invalid designation: " + designation);
		}

		// Fetch the Designation entity from DB for saving (avoid transient error)
		Designation dbDesignation = desgRepo.findAll().stream().filter(d -> d.getOrgDesignations() == designation)
				.findFirst().orElseThrow(() -> new IllegalArgumentException("Designation not found in DB"));
		emp.setDesignation(dbDesignation);

		// Call the appropriate handler based on designation
		switch (designation) {
		case CEO:
			return handleCeo(emp);
		case DEPT_HEAD:
			return handleDeptHead(emp);
		case MANAGER:
			return handleManager(emp);
		case DEVELOPER:
		case TESTER:
		case INTERN:
			return handleEmployee(emp);
		default:
			throw new IllegalArgumentException("Unknown designation type");
		}
	}

	public List<Designation> getDesignationList() {
		return desgRepo.findAll();
	}

	// ---------------- Private helper methods ----------------

	private Employee handleCeo(Employee emp) {
		if (emp.getReportTo() != null) {
			throw new IllegalArgumentException("CEO should not have a reportTo");
		}
		if (emp.getDepartment() != null) {
			throw new IllegalArgumentException("CEO should not belong to any department");
		}
		if (employeeRepo.existsByDesignation_OrgDesignations(OrgDesignations.CEO)) {
			throw new IllegalArgumentException("CEO already exists");
		}
		return employeeRepo.save(emp);
	}

	private Employee handleDeptHead(Employee emp) {
		validateDepartment(emp);

		// CEO must exist
		if (!employeeRepo.existsByDesignation_OrgDesignations(OrgDesignations.CEO)) {
			throw new IllegalArgumentException("Please add CEO before adding department head");
		}

		// Must report to CEO
		if (emp.getReportTo() == null) {
			throw new IllegalArgumentException("Department head must report to CEO");
		} else {
			Employee reportToEmp = employeeRepo.findById(emp.getReportTo().getId())
					.orElseThrow(() -> new IllegalArgumentException("reportTo employee not found"));
			if (reportToEmp.getDesignation().getOrgDesignations() != OrgDesignations.CEO) {
				throw new IllegalArgumentException("Department head must report to CEO");
			}
		}

		return employeeRepo.save(emp);
	}

	private Employee handleManager(Employee emp) {
		validateDepartment(emp);

		Long deptId = emp.getDepartment().getId();

		// Department head must exist in this department
		if (!employeeRepo.existsByDesignation_OrgDesignationsAndDepartment_Id(OrgDesignations.DEPT_HEAD, deptId)) {
			throw new IllegalArgumentException("Please add department head for this department first");
		}

		// Must report to DEPT_HEAD of the same department
		if (emp.getReportTo() == null) {
			throw new IllegalArgumentException("Manager must report to department head");
		} else {
			Employee reportToEmp = employeeRepo
					.findByIdAndDepartment_DepartmentName(emp.getReportTo().getId(),
							emp.getDepartment().getDepartmentName())
					.orElseThrow(() -> new IllegalArgumentException("reportTo employee not found"));
			if (reportToEmp.getDesignation().getOrgDesignations() != OrgDesignations.DEPT_HEAD) {
				throw new IllegalArgumentException("Manager must report to department head from same department");
			}
		}
		return employeeRepo.save(emp);
	}

	private Employee handleEmployee(Employee emp) {
		validateDepartment(emp);

		Long deptId = emp.getDepartment().getId();

		// Manager must exist in this department
		if (!employeeRepo.existsByDesignation_OrgDesignationsAndDepartment_Id(OrgDesignations.MANAGER, deptId)) {
			throw new IllegalArgumentException("Please add manager for this department first");
		}

		// Must report to manager of the same department
		if (emp.getReportTo() == null) {
			throw new IllegalArgumentException("Employee must report to manager");
		} else {
			Employee reportToEmp = employeeRepo
					.findByIdAndDepartment_DepartmentName(emp.getReportTo().getId(),
							emp.getDepartment().getDepartmentName())
					.orElseThrow(() -> new IllegalArgumentException("reportTo employee not found"));
			if (reportToEmp.getDesignation().getOrgDesignations() != OrgDesignations.MANAGER) {
				throw new IllegalArgumentException("Employee must report to manager");
			}
		}

		return employeeRepo.save(emp);
	}

	// Move employee to another department
	public Employee moveEmployeeToDepartment(Long empId, String depatrmentName, Long reportTo) {
		Employee emp = employeeRepo.findById(empId)
				.orElseThrow(() -> new IllegalArgumentException("Employee not found"));

		Employee reportToEmp = null;
		if(emp.getDesignation().getOrgDesignations() == OrgDesignations.DEPT_HEAD) {
			List<Employee> emplist = employeeRepo.findByDepartment_DepartmentNameAndDesignation_OrgDesignations(depatrmentName, OrgDesignations.DEPT_HEAD);
			if (emplist != null && !emplist.isEmpty()) {
				throw new IllegalArgumentException("Department Head alredy exsit");
			} else {
				reportToEmp = employeeRepo.findById(reportTo).get();
				if(reportToEmp.getDesignation().getOrgDesignations() != OrgDesignations.CEO) {
					throw new IllegalArgumentException("department Head must report to CEO");
				}
			}
		} else {
			reportToEmp = employeeRepo.findByIdAndDepartment_DepartmentName(reportTo, depatrmentName)
					.orElseThrow(() -> new IllegalArgumentException("reportTo employee not found"));
		}

		Department newDept = departmentRepo.findByDepartmentName(depatrmentName)
				.orElseThrow(() -> new IllegalArgumentException("Department not found"));

		// CEO cannot be moved
		if (emp.getDesignation().getOrgDesignations() == OrgDesignations.CEO) {
			throw new IllegalArgumentException("Cannot move CEO to a department");
		}
		emp.setReportTo(reportToEmp);

		// Update department
		emp.setDepartment(newDept);
		return employeeRepo.save(emp);
	}

	// View employees of a department
	public List<Employee> getEmployeesByDepartment(String departmentName) {
		if (!departmentRepo.findByDepartmentName(departmentName).isPresent()) {
			throw new IllegalArgumentException("Department not found");
		}
		return employeeRepo.findByDepartment_DepartmentName(departmentName);
	}

	// View employees reporting to a manager
	public List<Employee> getEmployeesReportingToManager(Long managerId) {
		Employee manager = employeeRepo.findById(managerId)
				.orElseThrow(() -> new IllegalArgumentException("Manager not found"));

		if (manager.getDesignation().getOrgDesignations() != OrgDesignations.MANAGER) {
			throw new IllegalArgumentException("Employee is not a manager");
		}

		// Filter employees in same department reporting to this manager
		return employeeRepo.findByReportTo_IdAndDepartment_Id(managerId, manager.getDepartment().getId());
	}

	private void validateDepartment(Employee emp) {
		if (emp.getDepartment() == null || emp.getDepartment().getDepartmentName() == null) {
			throw new IllegalArgumentException("Please provide a valid department");
		}

		Optional<Department> department = departmentRepo
				.findByDepartmentName(emp.getDepartment().getDepartmentName().toUpperCase());

		if (!department.isPresent()) {
			throw new IllegalArgumentException("Please provide a valid department");
		}

		// Set the existing department to ensure consistent reference
		emp.setDepartment(department.get());
	}

}
