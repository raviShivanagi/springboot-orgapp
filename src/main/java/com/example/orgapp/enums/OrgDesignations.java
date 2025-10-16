package com.example.orgapp.enums;

import java.util.Arrays;

public enum OrgDesignations {
	CEO,
	DEPT_HEAD,
    MANAGER,
    DEVELOPER,
    TESTER,
    INTERN;
	
	 public static boolean isValidOrgDesignation(String name) {
	        return Arrays.stream(OrgDesignations.values())
	                     .anyMatch(e -> e.name().equalsIgnoreCase(name));
	    }

}
