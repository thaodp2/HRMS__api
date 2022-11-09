package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.DeviceType;
import com.minswap.hrms.entities.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {
    List<Position> findByDepartmentId(Long departmentId);
}
