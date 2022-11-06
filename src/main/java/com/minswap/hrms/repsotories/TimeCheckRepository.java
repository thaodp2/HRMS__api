package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.TimeCheck;
import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import java.util.Date;

@Repository
public interface TimeCheckRepository extends JpaRepository<TimeCheck, Long> {

    @Query("SELECT new com.minswap.hrms.response.dto.TimeCheckDto( " +
            " tc.personId as id,  " +
            " tc.personId ,  " +
            " p.fullName as personName,  " +
            " tc.timeIn as date, " +
            " tc.timeIn as timeIn,  " +
            " tc.timeOut  as timeOut, " +
            " tc.inLate as inLate, " +
            " tc.outEarly as outEarly ,  " +
            " tc.ot, " +
            " tc.workingTime  as workingTime, " +
            " '' as  requestTypeName ) " +
            " FROM  " +
            " TimeCheck tc " +
            " Join Person p On p.personId = tc.personId" +
            " WHERE  " +
            " tc.personId  = :personId  " +
            " AND ((:fromDate IS NULL AND :toDate IS NULL) OR tc.timeIn BETWEEN :fromDate and :toDate) " +
            " ORDER BY tc.timeIn  ")
    Page<TimeCheckDto> getListMyTimeCheck(@Param("personId") Long personId,
                                          @Param("fromDate") Date fromDate,
                                          @Param("toDate") Date toDate,
                                          Pageable pageable);

    @Query(value = " SELECT rt.request_type_name " +
            "FROM request r " +
            "join request_type rt on r.request_type_id  = rt.request_type_id " +
            "WHERE r.person_id = :personId " +
            "AND r.request_type_id NOT IN (7,11)" +
            "AND date(:absentDate) BETWEEN date(r.start_time) and date(r.end_time) " +
            "AND r.status LIKE 'Approved' LIMIT  1", nativeQuery = true)
    String getMissTimeCheckReason(@Param("personId") Long personId,
                                  @Param("absentDate") Date absentDate);
    @Query("SELECT new com.minswap.hrms.response.dto.TimeCheckDto( " +
            " tc.personId as id,  " +
            " tc.personId as personId,  " +
            " p.fullName as personName,  " +
            " tc.timeIn as date, " +
            " tc.timeIn as timeIn,  " +
            " tc.timeOut  as timeOut, " +
            " tc.inLate as inLate, " +
            " tc.outEarly as outEarly ,  " +
            " tc.ot as ot, " +
            " tc.workingTime  as workingTime," +
            " '' as  requestTypeName ) " +
            " FROM  " +
            " TimeCheck tc " +
            " Join Person p On p.personId = tc.personId" +
            " WHERE  " +
            " 1 = 1  " +
            " AND (:search IS NULL OR p.fullName LIKE %:search%) " +
            " AND ((:fromDate IS NULL AND :toDate IS NULL) OR tc.timeIn BETWEEN :fromDate and :toDate) " +
            " ORDER BY tc.timeIn  ")
    Page<TimeCheckDto> getListTimeCheck(@Param("search") String search,
                                        @Param("fromDate") Date fromDate,
                                        @Param("toDate") Date toDate,
                                        Pageable pageable);
    @Query(" SELECT new com.minswap.hrms.response.dto.DailyTimeCheckDto(" +
           " tc.timeIn , " +
           " tc.timeOut , " +
           " tc.inLate , " +
           " tc.outEarly) " +
           " FROM TimeCheck tc " +
           " join Person p on p.personId = tc.personId " +
           " WHERE p.personId = :personId" +
           " and date(tc.timeIn) = date(:dateTime)")
    DailyTimeCheckDto getDailyTimeCheck(@Param("personId") Long personId,
                                        @Param("dateTime") Date dateTime);
}
