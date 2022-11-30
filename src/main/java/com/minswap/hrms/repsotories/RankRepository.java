package com.minswap.hrms.repsotories;

import com.minswap.hrms.entities.Position;
import com.minswap.hrms.entities.Rank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<Rank, Long> {
}
