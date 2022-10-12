package com.minswap.hrms.repsotories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minswap.hrms.entities.Person;

public interface PersonRepository extends JpaRepository<Person, Long>{
}
