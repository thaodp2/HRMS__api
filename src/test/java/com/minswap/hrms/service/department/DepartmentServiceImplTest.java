package com.minswap.hrms.service.department;

import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.DepartmentResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;

public class DepartmentServiceImplTest {
    @Mock
    DepartmentRepository departmentRepository;
    @Mock
    PositionRepository positionRepository;
    @Mock
    PersonRepository personRepository;

    @Mock
    Logger log;
    @InjectMocks
    DepartmentServiceImpl departmentServiceImpl;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetListDepartment() throws Exception {
        when(departmentRepository.getListDepartmentBySearch(anyString(), any())).thenReturn(new PageImpl<>(List.of(new DepartmentDto())));
        when(personRepository.getNumberOfEmplInDepartment(anyLong())).thenReturn(Integer.valueOf(0));

        ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> result = departmentServiceImpl.getListDepartment(1, 1, "search", "desc", "dir");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetMasterDataDepartment() throws Exception {
        when(departmentRepository.findByDepartmentNameContainsIgnoreCase(anyString())).thenReturn(List.of(new Department("departmentName")));
        when(departmentRepository.findAll()).thenReturn(List.of(new Department("departmentName")));

        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> result = departmentServiceImpl.getMasterDataDepartment("search");
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testGetDepartmentDetail() throws Exception {
        when(departmentRepository.getAllDepartmentId()).thenReturn(List.of(Long.valueOf(1)));
        when(departmentRepository.getDepartmentByDepartmentId(anyLong())).thenReturn(new Department("departmentName"));
        when(departmentRepository.getNumberOfEmployeeInDepartment(anyLong())).thenReturn(Integer.valueOf(0));
        when(departmentRepository.getPersonIdByDepartmentId(anyLong())).thenReturn(List.of(Long.valueOf(1)));
        when(positionRepository.getPositionsByDepartmentId(anyLong())).thenReturn(List.of(new Position("positionName", Long.valueOf(1))));

        ResponseEntity<BaseResponse<DepartmentResponse, Void>> result = departmentServiceImpl.getDepartmentDetail(Long.valueOf(1));
        Assert.assertEquals(HttpStatus.OK, result.getStatusCode());
    }


}