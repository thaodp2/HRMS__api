package com.minswap.hrms.repsotories;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.DateDto;
import com.minswap.hrms.response.dto.ListRequestDto;
import com.minswap.hrms.response.dto.RequestDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

//    @Query(" SELECT new com.minswap.hrms.response.dto.RequestDto(" +
//            " r.requestId as requestId, p.fullName as sender, rt.requestTypeName as requestTypeName, r.createDate as createDate, " +
//            " r.startTime as startTime, r.endTime as endTime, r.reason as reason, r.status as status, p2.fullName as receiver," +
//            " dt.deviceTypeName as deviceTypeName, r.approvalDate as approvalDate) " +
//            " from Request r " +
//            " left join RequestType rt on " +
//            " r.requestTypeId = rt.requestTypeId " +
//            " left join Person p on " +
//            " p.personId = r.personId " +
//            " left join Person p2 on " +
//            " p2.personId = p.managerId " +
//            " left join DeviceType dt on " +
//            " r.deviceTypeId = dt.deviceTypeId " +
//            " WHERE p.personId =:personId " +
//            " AND r.createDate BETWEEN :fromDate and :toDate ")
//    Page<RequestDto> getListRequestBySearch(@Param("personId") Long personId,
//                                            @Param("fromDate") Date fromDate,
//                                            @Param("toDate") Date toDate,
//                                            Pageable pageable);
    @Query("select new com.minswap.hrms.response.dto.RequestDto(" +
            "r.requestId as requestId, p.fullName as personName,rt.requestTypeId as requestTypeId, rt.requestTypeName as requestTypeName, r.createDate as createDate, " +
            "r.startTime as startTime, r.endTime as endTime, " +
            "r.reason as reason, r.status as status, p2.fullName as receiver, dt.deviceTypeId as deviceTypeId, r.approvalDate as approvalDate) " +
            "from Request r " +
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
    @Query("update Request r set r.status =:status, r.approvalDate =:approvalDate where r.requestId =:id")
    Integer updateStatusRequest(@Param("status") String status,
                                @Param("id") Long id,
                                @Param("approvalDate") Date approvalDate);


    @Modifying
    @Transactional
    @Query("UPDATE Request r set r.startTime =:startTime, r.endTime =:endTime, r.reason =:reason where r.requestId =:id")
    Integer updateLeaveBenefitRequest(@Param("id") Long id,
                                      @Param("startTime") Date startTime,
                                      @Param("endTime") Date endTime,
                                      @Param("reason") String reason);

    @Modifying
    @Transactional
    @Query("UPDATE Request r set r.deviceTypeId =:deviceTypeId, r.reason =:reason where r.requestId =:id")
    Integer updateDeviceRequest(@Param("id") Long id,
                                @Param("deviceTypeId") Long deviceTypeId,
                                @Param("reason") String reason);

    @Query("select r.status from Request r where r.requestId =:id")
    String getStatusOfRequestById(@Param("id") Long id);

    @Query("select r.createDate from Request r where r.requestId=:id")
    Date getCreateDateById(@Param("id") Long id);

    @Query("select new com.minswap.hrms.response.dto.DateDto(r.startTime, r.endTime) from Request r where r.requestId=:id")
    DateDto getStartAndEndTimeByRequestId(@Param("id") Long id);

    @Query("select r.requestTypeId from Request r where r.requestId =:id")
    Integer isRequestIdValid(@Param("id") Long id);

    @Query("select r.personId from Request r where r.requestId=:id")
    Long getPersonIdByRequestId(@Param("id") Long id);
}
