package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.Employee;

@Repository
public interface EmployeeRepository extends UserDetailsRepository<Employee> {
}
