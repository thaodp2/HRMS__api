package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.response.dto.PayrollDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.Date;
import java.util.Optional;

@Repository
@Transactional
public interface PayrollRepository extends JpaRepository<Salary, Long> {

    @Query(value = " SELECT  tc.working_time " +
                   " FROM time_check tc  " +
                   " WHERE date(tc.time_in ) = date(:date)" +
                   " AND person_id = :personId ", nativeQuery = true)
    Double getWorkingTime(@Param("date") Date date,
                        @Param("personId") Long personId);

    Optional<Salary> findByPersonIdAndMonthAndYear(Long personId, int month, Year year);

    @Query(value =" SELECT   " +
                  " SUM( CASE when date(r.start_time) = date(r.end_time)  then TIME_TO_SEC(TIMEDIFF(r.end_time, r.start_time))/3600  " +
                  "           when date(r.start_time) = date( :date )  then TIME_TO_SEC(TIMEDIFF(CONCAT(date(r.end_time), \" 00:00:00\") , r.start_time))/3600  " +
                  "           when date(r.end_time) = date(  :date )  then TIME_TO_SEC(TIMEDIFF( r.end_time, CONCAT(date(r.end_time), \" 00:00:00\")))/3600  " +
                  " else 1  " +
                  " end )  " +
                  " FROM request r  " +
                  " WHERE date(:date) BETWEEN date(r.start_time) and date(r.end_time) " +
                  " and r.status  = 'Approved' " +
                  " and r.request_type_id in(1,3,8,10) " +
                  " and r.person_id  = :personId ", nativeQuery = true)
    Double getAdditionalWorkInRequest(@Param("date") Date date,
                                      @Param("personId") Long personId);
    
    @Query(value = " SELECT new com.minswap.hrms.response.dto.PayrollDto(" +
                    "  p.personId, " +
                    "  p.fullName, " +
                    "  p.rollNumber ," +
                    "  0D as totalWork," +
                    "  0D as actualWork, " +
                    "  p.salaryBasic  as basicSalary, " +
                    "  (SELECT ob.hoursWorked  " +
                    "  FROM OTBudget ob " +
                    "  WHERE ob.month = :month " +
                    "  AND ob.year = :year " +
                    "  AND ob.personId = :personId) as otSalary , " +
                    "  ( " +
                    "  SELECT  " +
                    "   SUM(mf.fineAmount) " +
                    "  FROM " +
                    "   MonetaryFine mf " +
                    "  WHERE " +
                    "   mf.timeCheckId in " +
                    "     (SELECT tc.timeCheckId " +
                    "   FROM " +
                    "    TimeCheck tc " +
                    "   WHERE MONTH(tc.timeIn) = :month " +
                    "   AND YEAR(tc.timeIn) = :year " +
                    "   AND tc.personId = :personId)) as fineAmount," +
                    " p.salaryBonus ," +
                    " 0D as tax," +
                    " 0D as socialInsurance," +
                    " 0D as actuallyReceived )" +
                    " FROM Person p " +
                    " WHERE p.personId = :personId ")
    PayrollDto getDataSalary(@Param("month") int month,
                             @Param("year") Year year,
                             @Param("personId") Long personId);


    Optional<Salary> getFirstByMonthAndYearAndPersonId(int month, Year year, Long personId);
}
