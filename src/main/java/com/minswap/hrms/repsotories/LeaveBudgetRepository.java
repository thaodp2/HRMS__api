package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.Year;

@Repository
public interface LeaveBudgetRepository extends JpaRepository<LeaveBudget, Long> {

    @Query("SELECT new com.minswap.hrms.response.dto.LeaveBudgetDto(lb.leaveBudget, lb.numberOfDayOff, lb.remainDayOff) " +
            "from LeaveBudget lb " +
            "where lb.personId =:personId and lb.year =:year and lb.requestTypeId =:requestTypeId")
    LeaveBudgetDto getLeaveBudget(@Param("personId") Long personId,
                                                   @Param("year") Year year,
                                                   @Param("requestTypeId") Long requestTypeId);

    @Modifying
    @Transactional
    @Query("Update LeaveBudget lb set lb.numberOfDayOff =:numberOfDayOff, lb.remainDayOff =:remainDayOff " +
            "where lb.personId =:id and lb.year=:year")
    Integer updateAnnualLeaveBudget(@Param("id") Long id,
                                    @Param("numberOfDayOff") double numberOfDayOff,
                                    @Param("remainDayOff") double remainDayOff,
                                    @Param("year") Year year);
}
