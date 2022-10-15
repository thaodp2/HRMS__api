package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.BorrowRequestDetailDto;
import com.minswap.hrms.response.dto.RequestDetailDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestDetailRepository extends JpaRepository<Request, Long>{

    @Query("select new com.minswap.hrms.response.dto.RequestDetailDto(" +
            "r.requestId as requestId, p.fullName as sender, rt.requestTypeName as requestTypeName, r.createDate as createDate, r.startTime as startTime, r.endTime as endTime, e.image as image, " +
            "r.reason as reason, r.status as status, p2.fullName as receiver, r.approvalDate as approvalDate) " +
            "from Request r " +
            "left join Evidence e on " +
            "r.requestId = e.requestId " +
            "left join RequestType rt on " +
            "r.requestTypeId = rt.requestTypeId " +
            "left join Person p on " +
            "p.personId = r.personId " +
            "left join Person p2 on " +
            "p2.personId = p.managerId " +
            "WHERE r.requestId =:id")
    RequestDetailDto getEmployeeRequestDetail(@Param("id") Long id);

    @Query("select r.requestTypeId from Request r where r.requestId =:id")
    Long getRequestTypeIdByRequestId(@Param("id") Long id);

    @Query("select new com.minswap.hrms.response.dto.BorrowRequestDetailDto(" +
            "r.requestId as requestId, dt.deviceTypeName as deviceTypeName, " +
            "r.createDate as createDate, r.startTime as startTime, " +
            "r.reason as reason, r.status as status, p2.fullName as receiver, r.approvalDate as approvalDate) " +
            "from Request r " +
            "left join DeviceType dt on " +
            "r.deviceTypeId = dt.deviceTypeId " +
            "left join RequestType rt on " +
            "r.requestTypeId = rt.requestTypeId " +
            "left join Person p on " +
            "p.personId = r.personId " +
            "left join Person p2 on " +
            "p2.personId = p.managerId " +
            "WHERE r.requestId =:id")
    BorrowRequestDetailDto getBorrowRequestDetailDto(@Param("id") Long id);

}
