package com.example.orgapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.orgapp.entity.Designation;

public interface DesignationRepository extends JpaRepository<Designation, Long> {

}
