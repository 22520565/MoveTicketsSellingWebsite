package com.movie.main.repository;

import org.springframework.stereotype.Repository;

import com.movie.main.entity.Customer;

@Repository
public interface CustomerRepository extends UserDetailsRepository<Customer> {
}
