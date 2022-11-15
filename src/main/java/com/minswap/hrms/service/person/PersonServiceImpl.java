package com.minswap.hrms.service.person;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PersonServiceImpl implements PersonService{
    @Autowired
    private PersonRepository personRepository;

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest updateUserDto) throws Exception {
        try {
            ModelMapper modelMapper = new ModelMapper();
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(updateUserDto.getPersonId());

            if(!personFromDB.isPresent()){
                throw new Exception("Person not exist");
            }
            Person person = personFromDB.get();
            modelMapper.map(updateUserDto, person);
            personRepository.save(person);
        }catch (Exception ex){
            throw new Exception(ex.getMessage());
        }
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager(String search) {
        List<Person> personList = personRepository.getMasterDataAllManager(CommonConstant.ROLE_ID_OF_MANAGER, search.trim());
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        for (int i = 0; i < personList.size(); i++) {
            MasterDataDto masterDataDto = new MasterDataDto(personList.get(i).getFullName(), personList.get(i).getPersonId());
            masterDataDtos.add(masterDataDto);
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(String rollNumber) {
        EmployeeDetailDto employeeDetailDto = personRepository.getDetailEmployee(rollNumber);
        try {
            SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
            String dateOfBirthSm = sm.format(employeeDetailDto.getDateOfBirth());
            Date date = sm.parse(dateOfBirthSm);
            employeeDetailDto.setDateOfBirth(date);
            String dateOnBoardSm = sm.format(employeeDetailDto.getOnBoardDate());
            date = sm.parse(dateOnBoardSm);
            employeeDetailDto.setOnBoardDate(date);
            employeeDetailDto.setRole("1");
        }catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }

        EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null,employeeDetailDto);
        ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
                .ofSucceeded(employeeListDtos);

        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,Long departmentId,String rollNumber,String status,Long positionId, String managerRoll) {
        page = page - 1;
        Pagination pagination = new Pagination(page, limit);
        Long managerId = null;
        if(!StringUtils.isEmpty(managerRoll)) {
            Optional<Person> personByRollNumber = personRepository.findPersonByRollNumberEquals("NV003");
            if (!personByRollNumber.isPresent()) {
                throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
            }
            Person person = personByRollNumber.get();
            managerRoll = person.getPersonId().toString();
            managerId = Long.parseLong(managerRoll);
        }
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email,departmentId,rollNumber,positionId,managerId,pagination);
        List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
        pagination.setTotalRecords(pageInfo);
        pagination.setPage(page + 1);
        ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(EmployeeRequest employeeRequest, String rollNumber) {
        personRepository.updateEmployee(employeeRequest.getActive(),
                employeeRequest.getFullName(),
                employeeRequest.getManagerId(),
                employeeRequest.getDepartmentId(),
                employeeRequest.getPositionId(),
                employeeRequest.getRankId(),
                employeeRequest.getCitizenIdentification(),
                employeeRequest.getPhoneNumber(),
                employeeRequest.getAddress(),
                employeeRequest.getGender(),
                rollNumber);

        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest) {
        Person person = new Person();
        person.setFullName(employeeRequest.getFullName());
        person.setAddress(employeeRequest.getAddress());
        person.setCitizenIdentification(employeeRequest.getCitizenIdentification());
        person.setPhoneNumber(employeeRequest.getPhoneNumber());
        person.setRollNumber(convertRollNumber());
        person.setRankId(employeeRequest.getRankId());
        person.setDepartmentId(employeeRequest.getDepartmentId());
        person.setManagerId(employeeRequest.getManagerId());
        person.setGender(employeeRequest.getGender());
        person.setStatus("1");
        String convertMail = convertMail(employeeRequest.getFullName(), person.getRollNumber());
        person.setEmail(convertMail);
        person.setPositionId(employeeRequest.getPositionId());
        try {
            SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
            Date dateOfBirth = sm.parse(employeeRequest.getDateOfBirth());
            person.setDateOfBirth(dateOfBirth);
            Date dateOnBoard = sm.parse(employeeRequest.getOnBoardDate());
            person.setOnBoardDate(dateOnBoard);
        }catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
        try {
            personRepository.save(person);
        }catch (Exception e) {
            throw new BaseException(ErrorCode.newErrorCode(500, e.getMessage()));
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(ChangeStatusEmployeeRequest employeeRequest, String rollNumber) {
        personRepository.updateStatusEmployee(employeeRequest.getActive(),
                rollNumber);

        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public List<EmployeeListDto> exportEmployee(String fullName,String email,Long departmentId,String rollNumber,Long positionId) {
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email,departmentId,rollNumber,positionId,null,null);
        List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
        return employeeListDtos;
    }
    private String convertRollNumber() {
        long count = personRepository.count()+1;
        String rollNumber = "MS00"+ count;
        return rollNumber;
    }
    private String convertMail(String fullName, String rollNumber) {
        String removeName = removeAccent(fullName);
        String[] split = removeName.split("\\s");
        rollNumber = rollNumber.substring(rollNumber.length() - 2, rollNumber.length());
        String fMailName = split[0];
        String lMailName= split[split.length - 1];
        return lMailName + "."+ fMailName +rollNumber +"@minswap.com";
    }
    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }
}
