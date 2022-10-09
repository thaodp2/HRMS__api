package com.minswap.hrms.repository;

import com.minswap.hrms.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRespository extends JpaRepository<Role, Integer> {
}
