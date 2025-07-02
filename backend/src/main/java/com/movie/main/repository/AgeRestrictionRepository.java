package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.AgeRestriction;

@Repository
public interface AgeRestrictionRepository extends JpaRepository<AgeRestriction, Integer> {}
