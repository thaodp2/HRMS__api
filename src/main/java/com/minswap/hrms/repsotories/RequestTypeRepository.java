package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType,Integer> {
}
