package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RequestStatusRepository extends JpaRepository<Request, Long> {

    @Modifying
    @Transactional
    @Query("update Request r set r.status =:status where r.requestId =:id")
    Integer updateRequest(@Param("status") String status, @Param("id") Long id);

}
