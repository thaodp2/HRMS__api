package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.response.dto.PositionDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByDepartmentId(Long departmentId);
    List<Position> findByDepartmentIdAndPositionNameContainsIgnoreCase(Long departmentId, String search);
    List<Position> findByPositionNameContainsIgnoreCase(String search);
    List<Position> findAll();

    @Query("select p.positionId from Position p where p.positionName =:positionName " +
            "and (:id is null or p.departmentId <>:id)")
    Integer isPositionAlreadyExist(@Param("positionName") String positionName,
                                   @Param("id") Long id);
    @Modifying
    @Transactional
    @Query("delete from Position p where p.departmentId=:departmentId")
    Integer deletePositionByDepartmentId(@Param("departmentId") Long departmentId);

    List<Position> getPositionsByDepartmentId(Long id);

    @Query("select p.positionId from Position p where p.departmentId=:id")
    List<Long> getPositionIdsByDepartmentId(@Param("id") Long id);

    @Query("select p.positionName from Position p where p.positionId=:id")
    String getPositionNameByPositionId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("update Position p set p.positionName=:positionName where p.positionId=:positionId")
    Integer updatePosition(@Param("positionName") String positionName,
                           @Param("positionId") Long positionId);

    @Modifying
    @Transactional
    @Query("delete from Position p where p.positionId=:positionId")
    Integer deletePositionByPositionId(@Param("positionId") Long positionId);

    @Query("select new com.minswap.hrms.response.dto.PositionDto(p.positionId, p.positionName) " +
            "from Position p " +
            "where p.departmentId=:departmentId")
    List<PositionDto> getListPosition(@Param("departmentId") Long departmentId);
}
