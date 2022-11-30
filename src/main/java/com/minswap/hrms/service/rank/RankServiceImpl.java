package com.minswap.hrms.service.rank;

import com.minswap.hrms.entities.Rank;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.repsotories.RankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankServiceImpl implements RankService{

    @Autowired
    RankRepository rankRepository;

    @Override
    public boolean checkRankExist(Long rankId) {
        Rank rank = rankRepository.findById(rankId).orElse(null);
        if(rank != null){
            return true;
        }
        return false;
    }
}
