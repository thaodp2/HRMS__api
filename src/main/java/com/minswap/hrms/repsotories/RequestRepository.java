package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request,Integer> {
}
