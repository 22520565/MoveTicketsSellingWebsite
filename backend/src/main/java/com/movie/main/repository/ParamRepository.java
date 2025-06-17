package com.movie.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movie.main.entity.Param;

@Repository
public interface ParamRepository extends JpaRepository<Param, Integer> {}
