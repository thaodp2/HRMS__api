package com.minswap.hrms.repsotories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minswap.hrms.entities.Person;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long>{

    Optional<Person> findPersonByPersonId(Long id);
}
