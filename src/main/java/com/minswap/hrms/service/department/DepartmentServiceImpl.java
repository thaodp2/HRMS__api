package com.minswap.hrms.service.department;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Position;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.DepartmentRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PositionRepository;
import com.minswap.hrms.request.CreateDepartmentRequest;
import com.minswap.hrms.request.DepartmentRequest;
import com.minswap.hrms.response.DepartmentResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.DepartmentDto;
import com.minswap.hrms.response.dto.ListDepartmentDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.response.dto.PositionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    DepartmentRepository departmentRepository;
    @Autowired
    PositionRepository positionRepository;
    @Autowired
    PersonRepository personRepository;

    HttpStatus httpStatus;
    private static final String SORT_DESC = "desc";
    private static final String SORT_ASC = "asc";
    private static final int NOT_ALLOW_DELETE = 0;
    private static final int ALLOW_DELETE = 1;
    @Override
    public ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> getListDepartment(Integer page,
                                                                                       Integer limit,
                                                                                       String search,
                                                                                       String sort) {
        ResponseEntity<BaseResponse<ListDepartmentDto, Pageable>> responseEntity = null;
        Pagination pagination = new Pagination(page - 1, limit);
        Page<DepartmentDto> listDepartmentDto = departmentRepository.getListDepartmentBySearch(search, pagination);
        List<DepartmentDto> departmentDtos = listDepartmentDto.getContent();
        for (DepartmentDto departmentDto : departmentDtos) {
            departmentDto.setTotalEmployee(personRepository.getNumberOfEmplInDepartment(departmentDto.getId()));
            List<Long> listPersonId = departmentRepository.getPersonIdByDepartmentId(departmentDto.getId());
            if (listPersonId.size() > 0) {
                departmentDto.setIsAllowDelete(NOT_ALLOW_DELETE);
            }
            else {
                departmentDto.setIsAllowDelete(ALLOW_DELETE);
            }
        }
        if (sort != null && (sort.equalsIgnoreCase(SORT_ASC) || sort.equalsIgnoreCase(SORT_DESC))) {
            departmentDtos = listDepartmentDto.getContent()
                                               .stream().sorted((o1, o2) -> {
                                                   if (sort.equalsIgnoreCase(SORT_DESC)) {
                                                       if (o1.getTotalEmployee() > o2.getTotalEmployee()) {
                                                           return -1;
                                                       }
                                                       return 1;
                                                   }
                                                   else {
                                                       if (o1.getTotalEmployee() < o2.getTotalEmployee()) {
                                                           return -1;
                                                       }
                                                       return 1;
                                                   }
                                               })
                                               .collect(Collectors.toList());
        }
        pagination.setTotalRecords(listDepartmentDto);
        pagination.setPage(page);
        responseEntity = BaseResponse.ofSucceededOffset(ListDepartmentDto.of(departmentDtos), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createDepartment(CreateDepartmentRequest createDepartmentRequest) {
        String departmentName = getFormattedName(createDepartmentRequest.getDepartmentName());
        List<String> listPositionName = createDepartmentRequest.getListPosition();
        List<String> listFormattedPositionName = getListPositionNameAfterFormat(listPositionName);
        if(isDepartmentAlreadyExist(departmentName)) {
            throw new BaseException(ErrorCode.INVALID_DEPARTMENT);
        }
        else if (getListPositionAlreadyExist(listPositionName, null) != null) {
            String message = getMessageWhenPositionDuplicate(getListPositionAlreadyExist(listPositionName, null));
            throw new BaseException(ErrorCode.newErrorCode(208,
                                                                message,
                                                                httpStatus.ALREADY_REPORTED));
        }
        else if (createDepartmentRequest.getListPosition().isEmpty()) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "List position name can't be empty",
                    httpStatus.ALREADY_REPORTED));
        }
        else if (isListPositionNameDuplicateElement(listPositionName)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                    "In the same department can't have the same position!",
                    httpStatus.ALREADY_REPORTED));
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        Department department = new Department(departmentName);
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
    public ResponseEntity<BaseResponse<Void, Void>> editDepartment(Long id, DepartmentRequest departmentRequest) {
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = null;
        if (!isIdValid(id)) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Department ID not found!",
                    httpStatus.NOT_FOUND));
        }
        List<String> listPositionName = new ArrayList<>();
        for (PositionDto position : departmentRequest.getListPosition()) {
            listPositionName.add(position.getPositionName());
        }
        List<String> listDepartmentName = departmentRepository.getOtherDepartmentName(id);
        String formattedDepartName = getFormattedName(departmentRequest.getDepartmentName());
        for (String departName : listDepartmentName) {
            if (departName.trim().equalsIgnoreCase(formattedDepartName)) {
                throw new BaseException(ErrorCode.INVALID_DEPARTMENT);
            }
        }
        if (getListPositionAlreadyExist(listPositionName, id) != null) {
            String message = getMessageWhenPositionDuplicate(getListPositionAlreadyExist(listPositionName, id));
            throw new BaseException(ErrorCode.newErrorCode(208,
                                                                message,
                                                                httpStatus.ALREADY_REPORTED));
        }
        else if (departmentRequest.getListPosition().isEmpty()) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                                                        "List position name can't be empty",
                                                                httpStatus.ALREADY_REPORTED));
        }
        else if (isListPositionNameDuplicateElement(listPositionName)) {
            throw new BaseException(ErrorCode.newErrorCode(208,
                                                        "In the same department can't have the same position!",
                                                                httpStatus.ALREADY_REPORTED));
        }

        List<Long> listPosition = positionRepository.getPositionIdsByDepartmentId(id);
        List<Long> listPositionIdAfterEdit = new ArrayList<>();
        for (PositionDto position : departmentRequest.getListPosition()) {
            if (position.getPositionName().trim().isEmpty()) {
                throw new BaseException(ErrorCode.newErrorCode(406,
                        "Position can't be empty!",
                        httpStatus.NOT_ACCEPTABLE));
            }
            listPositionIdAfterEdit.add(position.getPositionId());
        }
        for (Long positionId : listPosition) {
            if (!listPositionIdAfterEdit.contains(positionId)) {
                if (personRepository.getListPersonIdByPositionId(positionId, "1").size() > 0) {
                    throw new BaseException(ErrorCode.newErrorCode(406,
                            "Can't remove position: " +
                                    positionRepository.getPositionNameByPositionId(positionId) +
                                    " because it's active",
                            httpStatus.NOT_ACCEPTABLE));
                }
                else {
                    positionRepository.deletePositionByPositionId(positionId);
                }
            }
        }

        Integer isUpdateSucceeded = departmentRepository.updateDepartment(formattedDepartName, id);
        if (isUpdateSucceeded == CommonConstant.UPDATE_SUCCESS) {
            for (PositionDto position : departmentRequest.getListPosition()) {
                if (position.getPositionId() == null) {
                    Position savingPosition = new Position(getFormattedName(position.getPositionName()),
                                                           id);
                    positionRepository.save(savingPosition);
                }
                else {
                    positionRepository.updatePosition(getFormattedName(position.getPositionName()),
                                                      position.getPositionId());
                }
            }
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
        if (!isIdValid(id)) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Department ID not found!",
                    httpStatus.NOT_FOUND));
        }
        else if (departmentRepository.getPersonIdByDepartmentId(id) != null) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "You can't delete this department because it is active",
                    httpStatus.NOT_ACCEPTABLE));
        }
        try {
            departmentRepository.deleteById(id);
            positionRepository.deletePositionByDepartmentId(id);
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

    @Override
    public ResponseEntity<BaseResponse<DepartmentResponse, Void>> getDepartmentDetail(Long id) {
        if (!isIdValid(id)) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Department ID not found!",
                    httpStatus.NOT_FOUND));
        }
        Department department = departmentRepository.getDepartmentByDepartmentId(id);
        List<Position> listPosition = positionRepository.getPositionsByDepartmentId(id);
        List<PositionDto> listPositionDto = new ArrayList<>();
        for (Position position : listPosition) {
            int isAllowDelete = 1;
            if (personRepository.getListPersonIdByPositionId(position.getPositionId(), "1").size() > 0) {
                isAllowDelete = 0;
            }
            listPositionDto.add(new PositionDto(isAllowDelete, position.getPositionId(), position.getPositionName()));
        }
        Integer numberOfEmployeeInDepartment = departmentRepository.getNumberOfEmployeeInDepartment(id);
        DepartmentDto departmentDto = new DepartmentDto(department.getDepartmentId(), department.getDepartmentName());
        departmentDto.setTotalEmployee(numberOfEmployeeInDepartment);
        departmentDto.setListPosition(listPositionDto);
        List<Long> listPersonId = departmentRepository.getPersonIdByDepartmentId(id);
        if (listPersonId.size() > 0) {
            departmentDto.setIsAllowDelete(NOT_ALLOW_DELETE);
        }
        else {
            departmentDto.setIsAllowDelete(ALLOW_DELETE);
        }
        DepartmentResponse departmentResponse = new DepartmentResponse(departmentDto);
        ResponseEntity<BaseResponse<DepartmentResponse, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(departmentResponse, null);
        return responseEntity;
    }

    @Override
    public boolean checkDepartmentExist(Long departmentId) {
        Department d = departmentRepository.findById(departmentId).orElse(null);
        if(d != null){
            return true;
        }
        return false;
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

    public List<String> getListPositionAlreadyExist(List<String> listPositionName, Long id) {
        List<String> listPositionNameAlreadyExist = new ArrayList<>();
        for (String positionName : listPositionName) {
            if (positionRepository.isPositionAlreadyExist(positionName, id) != null) {
                listPositionNameAlreadyExist.add(positionName);
            }
        }

        if (listPositionNameAlreadyExist.isEmpty()) {
            return null;
        }
        else {
            return listPositionNameAlreadyExist;
        }

    }

    public String getFormattedName(String name) {
        String[] splited = name.split("\\s+");
        String formattedName = "";
        for (int i = 0; i < splited.length; i++) {
            formattedName += splited[i] + " ";
        }
        return formattedName.trim();
    }

    public String getMessageWhenPositionDuplicate(List<String> listPositionNameAlreadyExist) {
        String message = "";
        String positionExist = "";
        for (String positionName : listPositionNameAlreadyExist) {
            positionExist += "'" + positionName + "', ";
        }
        positionExist = positionExist.replaceAll(", $", "");
        message = "Position name: " + positionExist + " already exist in another department";
        return message;
    }

    public boolean isIdValid(Long id) {
        if (departmentRepository.getAllDepartmentId().contains(id)) {
            return true;
        }
        return false;
    }

    public List<String> getListPositionNameAfterFormat(List<String> listPositionName) {
        List<String> listFormattedPositionName = new ArrayList<>();
        for (String positionName : listPositionName) {
            listFormattedPositionName.add(getFormattedName(positionName));
        }
        return listFormattedPositionName;
    }

    public boolean isListPositionNameDuplicateElement(List<String> listPositionName) {
        List<String> listPosName = getListPositionNameAfterFormat(listPositionName);
        Set<String> store = new HashSet<>();
        for (String positionName : listPosName) {
            if (store.add(positionName.toLowerCase()) == false){
                return true;
            }
        }
        return false;
    }

}
