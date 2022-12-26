package com.minswap.hrms.service.position;

import com.minswap.hrms.entities.Position;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.response.MasterDataResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;

public class PositionServiceImplTest {
    @Mock
    PositionRepository positionRepository;
    @Mock
    Logger log;
    @InjectMocks
    PositionServiceImpl positionServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetMasterDataPositionByDepartmentId() throws Exception {
        when(positionRepository.findByDepartmentId(anyLong())).thenReturn(List.of(new Position("positionName", Long.valueOf(1))));
        when(positionRepository.findByDepartmentIdAndPositionNameContainsIgnoreCase(anyLong(), anyString())).thenReturn(List.of(new Position("positionName", Long.valueOf(1))));
        when(positionRepository.findByPositionNameContainsIgnoreCase(anyString())).thenReturn(List.of(new Position("positionName", Long.valueOf(1))));
        when(positionRepository.findAll()).thenReturn(List.of(new Position("positionName", Long.valueOf(1))));

        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> result = positionServiceImpl.getMasterDataPositionByDepartmentId(Long.valueOf(1), "search");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testCheckPositionByDepartment() throws Exception {
        boolean result = positionServiceImpl.checkPositionByDepartment(Long.valueOf(1), Long.valueOf(1));
        Assert.assertEquals(false, result);
    }
}

