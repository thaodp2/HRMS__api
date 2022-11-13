package com.minswap.hrms.service.department;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    PositionRepository positionRepository;

    HttpStatus httpStatus;

    private static final int DEFAULT_TOTAL_EMPLOYEE = 0;

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
    public ResponseEntity<BaseResponse<Void, Void>> createDepartment(DepartmentRequest departmentRequest) {
        String departmentName = getFormattedName(departmentRequest.getDepartmentName());
        List<String> listPositionName = departmentRequest.getListPosition();
        List<String> listFormattedPositionName = new ArrayList<>();
        for (String posName : listPositionName) {
            listFormattedPositionName.add(getFormattedName(posName));
        }
        if(isDepartmentAlreadyExist(departmentName)) {
            throw new BaseException(ErrorCode.INVALID_DEPARTMENT);
        }
        else if (getPositionAlreadyExist(listPositionName) != null) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "Position name: '" + getPositionAlreadyExist(listPositionName) + "' already exist",
                    httpStatus.ALREADY_REPORTED));
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        Department department = new Department(departmentName,
                                               departmentRequest.getIsActive(),
                                               DEFAULT_TOTAL_EMPLOYEE);
        departmentRepository.save(department);
        Integer departmentIdJustAdded = departmentRepository.getLastDepartmentId();
        for (String positionName : listFormattedPositionName) {
            Position position = new Position(positionName, Long.valueOf(departmentIdJustAdded));
            positionRepository.save(position);
        }
        responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> editDepartment(Long id, String departmentName) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        List<String> listDepartmentName = departmentRepository.getOtherDepartmentName(id);
        String formattedDepartName = getFormattedName(departmentName);
        for (String departName : listDepartmentName) {
            if (departName.trim().equalsIgnoreCase(formattedDepartName)) {
                throw new BaseException(ErrorCode.INVALID_DEPARTMENT);
            }
        }
        Integer isUpdateSucceeded = departmentRepository.updateDepartment(formattedDepartName, id);
        if (isUpdateSucceeded == CommonConstant.UPDATE_SUCCESS) {
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

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataDepartment(String search) {
        List<Department> departments;
        if(search != null){
            departments = departmentRepository.findByDepartmentNameContainsIgnoreCase(search.trim());
        }else {
            departments = departmentRepository.findAll();
        }
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < departments.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(departments.get(i).getDepartmentName(), departments.get(i).getDepartmentId());
            masterDataDtos.add(masterDataDto);
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    public boolean isDepartmentAlreadyExist(String departmentName) {
        List<Department> listDepartment = departmentRepository.findAll();
        for (Department department : listDepartment) {
            if (department.getDepartmentName().equalsIgnoreCase(departmentName)) {
                return true;
            }
        }
        return false;
    }

    public String getPositionAlreadyExist(List<String> listPositionName) {
        List<Position> listPosition = positionRepository.findAll();
        List<String> listFormattedPositionName = new ArrayList<>();
        for (String positionName : listPositionName) {
            listFormattedPositionName.add(getFormattedName(positionName));
        }
        for (Position position : listPosition) {
            for (String positionName : listFormattedPositionName) {
                if (positionName.equalsIgnoreCase(position.getPositionName())) {
                    return positionName;
                }
            }
        }
        return null;
    }

    public String getFormattedName(String name) {
        String[] splited = name.split("\\s+");
        String formattedName = "";
        for (int i = 0; i < splited.length; i++) {
            formattedName += splited[i] + " ";
        }
        return formattedName.trim();
    }


}
