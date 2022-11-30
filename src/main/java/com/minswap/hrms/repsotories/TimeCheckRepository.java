package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.TimeCheck;
import com.minswap.hrms.response.dto.DailyTimeCheckDto;
import com.minswap.hrms.response.dto.TimeCheckDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface TimeCheckRepository extends JpaRepository<TimeCheck, Long> {

    @Query("SELECT new com.minswap.hrms.response.dto.TimeCheckDto( " +
            " tc.personId as id,  " +
            " tc.personId ,  " +
            " p.fullName as personName," +
            " p.rollNumber as rollNumber,  " +
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
            " p.fullName as personName," +
            " p.rollNumber as rollNumber, " +
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

    @Query("select tc.timeIn from TimeCheck tc " +
            "where tc.personId=:personId " +
            "and DAY(tc.timeIn) =:timeIn")
    Date getTimeInOfPersonByDay(@Param("personId") Long personId,
                                @Param("timeIn") int timeIn);

    @Modifying
    @Transactional
    @Query("update TimeCheck tc " +
            "set tc.inLate=:inLate, " +
            "tc.outEarly=:outEarly, " +
            "tc.timeIn=:timeIn, " +
            "tc.timeOut=:timeOut, " +
            "tc.ot=:ot, " +
            "tc.workingTime=:workingTime " +
            "where tc.personId=:personId and DAY(tc.timeIn) =:dayOfTimeIn")
    Integer updateTimeCheckOfEmployee(@Param("personId") Long personId,
                                      @Param("inLate") double inLate,
                                      @Param("outEarly") double outEarly,
                                      @Param("timeIn") Date timeIn,
                                      @Param("timeOut") Date timeOut,
                                      @Param("ot") double ot,
                                      @Param("workingTime") double workingTime,
                                      @Param("dayOfTimeIn") int dayOfTimeIn);

    @Query("select tc.ot from TimeCheck tc " +
            "where tc.personId=:personId " +
            "and DAY(tc.timeIn) =:dayIn")
    Double getOTTimeByDay(@Param("dayIn") int dayIn,
                          @Param("personId") Long personId);

    @Modifying
    @Transactional
    @Query("update TimeCheck tc set tc.ot=:otTime where tc.personId=:personId and DAY(tc.timeIn) =:dayOfTimeIn")
    Integer updateOTTime(@Param("dayOfTimeIn") int dayOfTimeIn,
                         @Param("personId") Long personId,
                         @Param("otTime") double otTime);
    @Modifying
    @Transactional
    @Query("UPDATE TimeCheck t set " +
            "t.timeOut = :timeOut," +
            "t.outEarly = :outEarly," +
            "t.workingTime = :workingTime "+
            "where t.personId = :personId")
    Integer updateTimeCheck(@Param("timeOut")  Date timeOut,
                            @Param("outEarly")  Double  outEarly,
                            @Param("workingTime")  Double  workingTime,
                            @Param("personId") Long personId);

    @Modifying
    @Transactional
    @Query("delete from TimeCheck tc where day(tc.timeIn) =:day and MONTH(tc.timeIn) =:month and YEAR (tc.timeIn) =:year")
    Integer deleteTimeCheckByDate(@Param("day") int day,
                                  @Param("month") int month,
                                  @Param("year") int year);
}
