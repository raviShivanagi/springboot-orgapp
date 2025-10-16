package com.example.orgapp.dto;

public class EmployeeDTO {
    private Long id;
    private String name;
    private String mobileNumber;
    private String department;   // department name
    private String designation;  // designation name
    private Long reportTo;       // only manager ID

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Long getReportTo() { return reportTo; }
    public void setReportTo(Long reportTo) { this.reportTo = reportTo; }
}
