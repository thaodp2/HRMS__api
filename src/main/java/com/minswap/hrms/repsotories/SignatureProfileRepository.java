package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignatureProfileRepository extends JpaRepository<SignatureProfile, Long>{

    Optional<SignatureProfile> findSignatureProfileByPrivateKeySignature(String privateKeySignature);

//    @Query("select new com.minswap.hrms.response.dto.SignatureProfileDto(" +
//            "  s.privateKeySignature as privateKeySignature, s.personId as personId, s.registeredDate as registeredDate)" +
//            "  FROM SignatureProfile s " +
//            "     where  1 = 1  "+
//            " and s.personId = '-1'")
//    Page<SignatureProfileDto> getSearchListPerson(Pageable pageable);
}
