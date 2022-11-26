package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.DateDto;
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

    @Query("select new com.minswap.hrms.response.dto.RequestDto(" +
            "r.requestId as requestId, p.rollNumber as rollNumber, p.fullName as personName,rt.requestTypeId as requestTypeId, rt.requestTypeName as requestTypeName, r.createDate as createDate, " +
            "r.startTime as startTime, r.endTime as endTime, " +
            "r.reason as reason, r.status as status, p2.fullName as receiver, dt.deviceTypeId as deviceTypeId, dt.deviceTypeName as deviceTypeName, r.approvalDate as approvalDate, r.isAssigned as isAssigned) " +
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
    Integer updateLeaveOrOTRequest(@Param("id") Long id,
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

    @Query("select max(r.requestId) from Request r")
    Integer getLastRequestId();

    @Modifying
    @Transactional
    @Query("update Request r set r.status =:rejected where (r.startTime between :start and :end) and r.status=:pending")
    Integer autoRejectRequestNotProcessed(@Param("start") Date start,
                                          @Param("end") Date end,
                                          @Param("rejected") String rejected,
                                          @Param("pending") String pending);

    @Query("select r.startTime, r.endTime " +
           "from Request r " +
            "where r.personId=:personId " +
            "and r.requestTypeId=:requestTypeId " +
            "and ((r.startTime between :start and :end) or (r.endTime between :start and :end)) " +
            "and r.status=:status")
    List<DateDto> getListOTRequestApprovedByDate(@Param("personId") Long personId,
                                              @Param("requestTypeId") Long requestTypeId,
                                              @Param("start") Date start,
                                              @Param("end") Date end,
                                              @Param("status") String status);



    @Query("select new com.minswap.hrms.response.dto.RequestDto(" +
            "r.requestId as requestId, p.rollNumber as rollNumber, p.fullName as personName, r.createDate as createDate, r.reason as reason, " +
            "p2.fullName as receiver, dt.deviceTypeName as deviceTypeName, r.approvalDate as approvalDate, r.isAssigned as isAssigned) " +
            "from Request r " +
            "left join DeviceType dt on " +
            "r.deviceTypeId = dt.deviceTypeId " +
            "left join Person p on " +
            "r.personId = p.personId " +
            "left join Person p2 on " +
            "p2.personId = p.managerId " +
            "WHERE r.requestTypeId = 11 and r.status = 'Approved' " +
            "and (:search IS NULL OR p.fullName like %:search%) " +
            "and ((:fromDate IS NULL and :toDate IS NULL) OR (r.approvalDate BETWEEN :fromDate and :toDate )) " +
            "and (:isAssigned IS NULL OR r.isAssigned = :isAssigned) " +
            "and (:deviceTypeId IS NULL OR dt.deviceTypeId = :deviceTypeId)")
    Page<RequestDto> getBorrowDeviceRequestList(@Param("search") String search,
                                                @Param("fromDate") Date fromDate,
                                                @Param("toDate") Date toDate,
                                                @Param("deviceTypeId") Long deviceTypeId,
                                                @Param("isAssigned") Integer isAssigned,
                                                Pageable pageable);
}
