package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Evidence;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    @Query("select e.image from Evidence e " +
            "left join Request r on r.requestId = e.requestId where r.requestId =:id")
    List<String> getListImageByRequest(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("delete from Evidence e where e.requestId =:id")
    Integer deleteImageByRequestId(@Param("id") Long id);


}
