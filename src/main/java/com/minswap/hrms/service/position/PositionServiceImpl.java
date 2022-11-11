package com.minswap.hrms.service.position;

import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PositionServiceImpl implements PositionService{
    @Autowired
    PositionRepository positionRepository;

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataPositionByDepartmentId(Long departmentId, String search) {
        List<Position> positions;
        if(search != null){
            positions = positionRepository.findByDepartmentIdAndPositionNameContainsIgnoreCase(departmentId, search.trim());
        }else {
            positions = positionRepository.findByDepartmentId(departmentId);
        }
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < positions.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(positions.get(i).getPositionName(), positions.get(i).getPositionId());
            masterDataDtos.add(masterDataDto);
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }
}
