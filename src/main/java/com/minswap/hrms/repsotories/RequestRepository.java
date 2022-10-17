package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.RequestDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("select new com.minswap.hrms.response.dto.RequestDto(" +
            "r.requestId as requestId, p.fullName as personName, rt.requestTypeName as requestTypeName, r.createDate as createDate, " +
            "r.startTime as startTime, r.endTime as endTime, e.image as image, " +
            "r.reason as reason, r.status as status, p2.fullName as receiver, dt.deviceTypeName as deviceTypeName, r.approvalDate as approvalDate) " +
            "from Request r " +
            "left join Evidence e on " +
            "r.requestId = e.requestId " +
            "left join RequestType rt on " +
            "r.requestTypeId = rt.requestTypeId " +
            "left join Person p on " +
            "p.personId = r.personId " +
            "left join Person p2 on " +
            "p2.personId = p.managerId " +
            "left join DeviceType dt on " +
            "r.deviceTypeId = dt.deviceTypeId " +
            "WHERE r.requestId =:id")
    RequestDto getEmployeeRequestDetail(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update Request r set r.status =:status where r.requestId =:id")
    Integer updateRequest(@Param("status") String status, @Param("id") Long id);
}
