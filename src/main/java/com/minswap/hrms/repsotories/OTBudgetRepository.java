package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.OTBudget;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.response.dto.OTBudgetDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Year;

@Repository
public interface OTBudgetRepository extends JpaRepository<OTBudget, Long> {
    @Query("SELECT new com.minswap.hrms.response.dto.OTBudgetDto(ob.otHoursBudget, ob.hoursWorked) " +
            "from OTBudget ob " +
            "where ob.personId =:id and ob.year =:year and ob.month =:month")
    OTBudgetDto getOTBudgetByPersonId(@Param("id") Long id,
                                      @Param("year") Year year,
                                      @Param("month") int month);
    @Modifying
    @Transactional
    @Query("Update OTBudget ob set ob.hoursWorked =:hoursWorked " +
            "where ob.personId =:personId and ob.year=:year and ob.month =:month")
    Integer updateOTBudget(@Param("personId") Long personId,
                           @Param("year") Year year,
                           @Param("month") int month,
                           @Param("hoursWorked") double hoursWorked);

    @Query("SELECT new com.minswap.hrms.response.dto.BenefitBudgetDto(ob.otBudgetId as id, p.fullName as fullName, " +
            "ob.otHoursBudget as budget, ob.hoursWorked as used, ob.timeRemainingOfMonth as remainOfMonth, ob.timeRemainingOfYear as remainOfYear) " +
            "from OTBudget ob inner join Person p on ob.personId = p.personId " +
            "where ob.month = :month and ob.year = :year and (:search IS NULL OR p.fullName like %:search%) " +
            "and (:managerId IS NULL OR p.managerId = :managerId) " +
            "and (:personId IS NULL OR p.personId = :personId)")
    Page<BenefitBudgetDto> getBenefitBudgetList(@Param("month") Integer month,
                                                @Param("year") Year year,
                                                @Param("search") String search,
                                                @Param("managerId") Long managerId,
                                                @Param("personId") Long personId,
                                                Pageable pageable);
}
