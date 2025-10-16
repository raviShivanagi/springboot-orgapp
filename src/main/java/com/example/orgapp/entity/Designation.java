package com.example.orgapp.entity;

import com.example.orgapp.enums.OrgDesignations;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "designation")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Designation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "name")
	private OrgDesignations orgDesignations;

	@ManyToOne
	@JoinColumn(name = "parent_designation_id")
	private Designation parentDesignation;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OrgDesignations getOrgDesignations() {
		return orgDesignations;
	}

	public void setOrgDesignations(OrgDesignations orgDesignations) {
		this.orgDesignations = orgDesignations;
	}

	public Designation getParentDesignation() {
		return parentDesignation;
	}

	public void setParentDesignation(Designation parentDesignation) {
		this.parentDesignation = parentDesignation;
	}

}
