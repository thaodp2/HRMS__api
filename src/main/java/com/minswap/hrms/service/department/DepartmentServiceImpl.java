package com.minswap.hrms.service.department;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.response.dto.ListRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    DepartmentRepository departmentRepository;
    @Override
    public ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> getListDepartment(Integer page,
                                                                                       Integer limit,
                                                                                       String departmentName) {
        ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> responseEntity = null;
        Pagination pagination = new Pagination(page, limit);
        Page<DepartmentDto> listDepartmentDto = departmentRepository.getListDepartmentBySearch(departmentName, pagination);
        List<DepartmentDto> departmentDtos = listDepartmentDto.getContent();
        pagination.setTotalRecords(listDepartmentDto);
        responseEntity = BaseResponse.ofSucceededOffset(ListDepartmentDto.of(departmentDtos), pagination);
        return responseEntity;
    }

}
