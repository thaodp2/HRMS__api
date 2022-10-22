package com.minswap.hrms.service.department;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.response.dto.ListRequestDto;
import io.lettuce.core.dynamic.annotation.Param;
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

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createDepartment(String departmentName) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        Department department = new Department(departmentName);
        departmentRepository.save(department);
        responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editDepartment(Long id, String departmentName) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        List<String> listDepartmentName = departmentRepository.getOtherDepartmentName(id);
        String[] splited = departmentName.split("\\s+");
        String formatedDepartName = "";
        for (int i = 0; i < splited.length; i++) {
            formatedDepartName += splited[i] + " ";
        }

        for (String departName : listDepartmentName) {
            if (departName.trim().equalsIgnoreCase(formatedDepartName.trim())) {
                throw new BaseException(ErrorCode.UPDATE_DEPARTMENT_FAIL);
            }
        }
        Integer isUpdateSuccessed = departmentRepository.updateDepartment(formatedDepartName, id);
        if (isUpdateSuccessed == CommonConstant.UPDATE_SUCCESS) {
            responseEntity = BaseResponse.ofSucceeded(null);
        }
        else {
            throw new BaseException(ErrorCode.UPDATE_FAIL);
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> deleteDepartment(Long id) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        try {
            departmentRepository.deleteById(id);
            responseEntity = BaseResponse.ofSucceeded(null);
            return responseEntity;
        }
        catch (Exception e) {
            throw new BaseException(ErrorCode.DELETE_FAIL);
        }

    }


}
