package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Salary;
import com.minswap.hrms.response.dto.PayrollDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.Date;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Salary, Long> {

    @Query(value = " SELECT  tc.working_time " +
                   " FROM time_check tc  " +
                   " WHERE date(tc.time_in ) = date(:date)" +
                   " AND person_id = :personId ", nativeQuery = true)
    Double getWorkingTime(@Param("date") Date date,
                        @Param("personId") Long personId);

    Optional<Salary> findByPersonIdAndMonthAndYear(Long personId, int month, Year year);

    @Query(value = " SELECT TIMEDIFF(time(r.end_time), time(r.start_time))  as diff " +
           " FROM request r  " +
           " WHERE date(:date) BETWEEN date(r.start_time) and date(r.end_time) " +
           " and r.status  = 'Approved' " +
           " and r.request_type_id in(1,3,8,10) " +
           " and r.person_id  = :personId " +
           " limit 1 ", nativeQuery = true)
    String getAdditionalWorkInRequest(@Param("date") Date date,
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

    @Query(value = "  SELECT s.salary_id from salary  s " +
                    " where s.month = :month " +
                    " and s.year = :year " +
                    " and s.person_id = :personId LIMIT 1 ", nativeQuery = true)
    Long getSalaryId(@Param("month") int month,
                     @Param("year") Year year,
                     @Param("personId") Long personId);
}
