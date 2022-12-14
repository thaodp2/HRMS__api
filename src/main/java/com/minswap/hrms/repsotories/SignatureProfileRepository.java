package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SignatureProfileRepository extends JpaRepository<SignatureProfile, Long> {

	List<SignatureProfile> findSignatureProfilesByPersonId(Long personId);

	Optional<SignatureProfile> findSignatureProfileByRegisteredDate(Date registeredDate);

	Optional<SignatureProfile> findSignatureProfileByPrivateKeySignature(String privateKeySignature);
}
