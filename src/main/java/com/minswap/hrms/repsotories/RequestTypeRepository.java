package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType,Long> {

    @Query("select rt.requestTypeId from RequestType rt")
    List<Long> getAllRequestTypeId();
}
