package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.ListRequestDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

//    @Query(" SELECT new com.minswap.hrms.response.dto.ListRequestDto(" +
//            " r.requestId as requestId, p.fullName as sender, rt.requestTypeName as requestTypeName, r.createDate as createDate, " +
//            " r.startTime as startTime, r.endTime as endTime, e.image as image, " +
//            " r.reason as reason, r.status as status, p2.fullName as receiver, dt.deviceTypeName as deviceTypeName, r.approvalDate as approvalDate) " +
//            " from Request r " +
//            " left join Evidence e on " +
//            " r.requestId = e.requestId " +
//            " left join RequestType rt on " +
//            " r.requestTypeId = rt.requestTypeId " +
//            " left join Person p on " +
//            " p.personId = r.personId " +
//            " left join Person p2 on " +
//            " p2.personId = p.managerId " +
//            " left join DeviceType dt on " +
//            " r.deviceTypeId = dt.deviceTypeId " +
//            " WHERE p.personId =:id " +
//            " AND r.createDate BETWEEN :fromDate and :toDate ")
//    ListRequestDto getListRequestBySearch(@Param("personId") Long id,
//                                          @Param("fromDate") String fromDate,
//                                          @Param("toDate") String toDate,
//                                          @Param("page") Integer page,
//                                          @Param("limit") Integer limit);
}