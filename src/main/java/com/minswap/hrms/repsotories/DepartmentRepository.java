package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.RequestType;
import com.minswap.hrms.response.dto.DepartmentDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    @Query("SELECT new com.minswap.hrms.response.dto.DepartmentDto(d.departmentId, d.departmentName,d.status, d.total) " +
            "from Department d " +
            "where (:departmentName IS NULL OR d.departmentName like %:departmentName%) " +
            "and (:status is null or d.status =:status)")
    Page<DepartmentDto> getListDepartmentBySearch(@Param("departmentName") String departmentName,
                                                  @Param("status") Integer status,
                                                  Pageable pageable);

    @Query("select d.departmentName from Department d where d.departmentId <>:id")
    List<String> getOtherDepartmentName(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE Department d set d.departmentName =:departmentName where d.departmentId =:id")
    Integer updateDepartment(@Param("departmentName") String departmentName,
                             @Param("id") Long id);

    List<Department> findByDepartmentNameContainsIgnoreCase(String deviceTypeName);

    List<Department> findAll();

    @Query("select max(d.departmentId) from Department d")
    Integer getLastDepartmentId();
}
