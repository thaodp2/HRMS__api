package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.PersonRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
@Transactional
public interface PersonRoleRepository extends JpaRepository<PersonRole, Long>, JpaSpecificationExecutor<PersonRole> {

    List<PersonRole> findByPersonId(Long personId);

    Optional<PersonRole> findByPersonIdAndAndRoleId(Long personId, Long roleId);

}
