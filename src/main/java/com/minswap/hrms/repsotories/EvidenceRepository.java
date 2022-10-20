package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Evidence;
import com.minswap.hrms.entities.Request;
import com.minswap.hrms.response.dto.EvidenceDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    @Query("select new com.minswap.hrms.response.dto.EvidenceDto(e.evidenceId, e.requestId, e.image) from Evidence e " +
            "left join Request r on r.requestId = e.requestId where r.requestId =:id")
    List<EvidenceDto> getListEvidenceByRequest(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("delete from Evidence e where e.requestId =:id")
    Integer deleteImageByRequestId(@Param("id") Long id);


}
