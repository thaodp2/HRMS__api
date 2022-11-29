package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.RequestType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestTypeRepository extends JpaRepository<RequestType,Long> {

    @Query("select rt.requestTypeId from RequestType rt")
    List<Long> getAllRequestTypeId();

    @Query("select r.requestTypeId from Request r where r.requestId =:id")
    Integer getRequestTypeByRequestId(@Param("id") Long id);

    @Query("select rt.requestTypeName " +
            "from RequestType rt " +
            "left join Request r " +
            "on r.requestTypeId = rt.requestTypeId " +
            "where r.requestId=:id")
    String getRequestTypeNameByRequestId(@Param("id") Long id);
}
