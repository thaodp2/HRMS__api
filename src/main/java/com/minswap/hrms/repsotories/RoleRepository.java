package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
@Transactional
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    List<Role> findByRoleIdIn(List<Long> roleIds);

}
