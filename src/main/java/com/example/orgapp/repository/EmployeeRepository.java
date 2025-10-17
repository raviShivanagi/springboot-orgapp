package com.example.orgapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orgapp.entity.Employee;
import com.example.orgapp.enums.OrgDesignations;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

	boolean existsByMobileNumber(String mobileNumber);

	boolean existsByDesignation_OrgDesignations(OrgDesignations designation);

	boolean existsByDesignation_OrgDesignationsAndDepartment_Id(OrgDesignations designation, Long deptId);

	Optional<Employee> findByDesignation_OrgDesignationsAndDepartment_Id(OrgDesignations designation, Long deptId);

	List<Employee> findByDepartment_Id(Long deptId);

	List<Employee> findByReportTo_Id(Long managerId);

	Optional<Employee> findByIdAndDepartment_DepartmentName(Long id, String departmentName);

	List<Employee> findByReportTo_IdAndDepartment_Id(Long reportToId, Long departmentId);

	List<Employee> findByDepartment_DepartmentName(String departmentName);

	List<Employee> findByDepartment_DepartmentNameAndDesignation_OrgDesignations(String departmentName, OrgDesignations designation);

}
