package com.example.orgapp.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.orgapp.dto.EmployeeDTO;
import com.example.orgapp.entity.Department;
import com.example.orgapp.entity.Designation;
import com.example.orgapp.entity.Employee;
import com.example.orgapp.service.OrgService;

@RestController
@RequestMapping("/api")
public class OrgController {

	private final OrgService orgService;

	public OrgController(OrgService orgService) {
		this.orgService = orgService;
	}

	@PostMapping("/departments")
	public ResponseEntity<?> addDepartment(@RequestBody Department dept) {
		Department savedDept = orgService.addDepartment(dept);
		if (savedDept == null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Department already exists");
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(savedDept);
	}

	@PostMapping("/employee")
	public ResponseEntity<?> addEmployee(@RequestBody Employee emp) {
		try {
			Employee savedEmp = orgService.addEmployee(emp);
			return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedEmp));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Unexpected error: " + e.getMessage());
		}
	}

	@GetMapping("/designation")
	public ResponseEntity<?> getDesignations() {
		List<Designation> designationList = orgService.getDesignationList();
		return ResponseEntity.status(HttpStatus.OK).body(designationList);
	}

	// Move employee to another department
	@PutMapping("/employee/{empId}/move/{departmentName}/{reportTo}")
	public ResponseEntity<?> moveEmployee(@PathVariable Long empId, @PathVariable String departmentName,@PathVariable Long reportTo) {
	    try {
	        Employee updatedEmp = orgService.moveEmployeeToDepartment(empId, departmentName, reportTo);
	        return ResponseEntity.ok(convertToDTO(updatedEmp));
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

	// View employees of a department
	@GetMapping("/department/{departmentName}/employees")
	public ResponseEntity<?> getEmployeesOfDepartment(@PathVariable String departmentName) {
	    try {
	        List<Employee> employees = orgService.getEmployeesByDepartment(departmentName);
	        return ResponseEntity.ok(employees.stream()
	                .map(this::convertToDTO)
	                .toList());
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}

	// View employees reporting to a manager
	@GetMapping("/manager/{managerId}/employees")
	public ResponseEntity<?> getEmployeesReportingToManager(@PathVariable Long managerId) {
	    try {
	        List<Employee> employees = orgService.getEmployeesReportingToManager(managerId);
	        return ResponseEntity.ok(employees.stream()
	                .map(this::convertToDTO)
	                .toList());
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}
	public EmployeeDTO convertToDTO(Employee emp) {
	    EmployeeDTO dto = new EmployeeDTO();
	    dto.setId(emp.getId());
	    dto.setName(emp.getName());
	    dto.setMobileNumber(emp.getMobileNumber());

	    if(emp.getDepartment() != null) {
	        dto.setDepartment(emp.getDepartment().getDepartmentName());	
	    }
	    if(emp.getDesignation() != null) {
	        dto.setDesignation(emp.getDesignation().getOrgDesignations().toString());
	    }
	    if(emp.getReportTo() != null) {
	        dto.setReportTo(emp.getReportTo().getId());
	    }
	    return dto;
	}


}
