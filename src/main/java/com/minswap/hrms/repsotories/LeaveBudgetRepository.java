package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.response.dto.BenefitBudgetDto;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
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
import java.util.List;

@Repository
public interface LeaveBudgetRepository extends JpaRepository<LeaveBudget, Long> {

    LeaveBudget findByPersonIdAndYearAndRequestTypeId(Long personId, Year year, Long requestTypeId);

    @Query("SELECT new com.minswap.hrms.response.dto.LeaveBudgetDto(lb.leaveBudget, lb.numberOfDayOff, lb.remainDayOff) " +
            "from LeaveBudget lb " +
            "where lb.personId =:personId and lb.year =:year and lb.requestTypeId =:requestTypeId")
    LeaveBudgetDto getLeaveBudget(@Param("personId") Long personId,
                                  @Param("year") Year year,
                                  @Param("requestTypeId") Long requestTypeId);

    @Modifying
    @Transactional
    @Query("Update LeaveBudget lb set lb.numberOfDayOff =:numberOfDayOff, lb.remainDayOff =:remainDayOff " +
            "where lb.personId =:personId and lb.year=:year and lb.requestTypeId =:requestTypeId")
    Integer updateLeaveBudget(@Param("personId") Long personId,
                              @Param("numberOfDayOff") double numberOfDayOff,
                              @Param("remainDayOff") double remainDayOff,
                              @Param("year") Year year,
                              @Param("requestTypeId") Long requestTypeId);

    @Query("SELECT new com.minswap.hrms.response.dto.BenefitBudgetDto(lb.leaveBudgetId as id, p.fullName as fullName, " +
            "lb.leaveBudget as budget, lb.numberOfDayOff as used, lb.remainDayOff as remain, rt.requestTypeName as requestTypeName) " +
            "from LeaveBudget lb LEFT join Person p on lb.personId = p.personId " +
            "left join RequestType rt on lb.requestTypeId = rt.requestTypeId " +
            "where lb.year = :year and (:search IS NULL OR p.fullName like %:search%) " +
            "and (:managerId IS NULL OR p.managerId = :managerId) " +
            "and (:requestTypeId IS NULL OR lb.requestTypeId = :requestTypeId) " +
            "and (:personId IS NULL OR p.personId = :personId)")
    Page<BenefitBudgetDto> getBenefitBudgetList(@Param("year") Year year,
                                                @Param("search") String search,
                                                @Param("managerId") Long managerId,
                                                @Param("requestTypeId") Long requestTypeId,
                                                @Param("personId") Long personId,
                                                Pageable pageable);
}
