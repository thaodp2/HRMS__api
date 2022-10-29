package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.TimeCheck;
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

    @Query("SELECT new com.minswap.hrms.response.dto.TimeCheckDto( " +
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
}
