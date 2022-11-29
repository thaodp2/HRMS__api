package com.minswap.hrms.service.person;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.PersonRole;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.request.ChangeStatusEmployeeRequest;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.request.EmployeeUpdateRequest;
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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository personRepository;

    private static final long MILLISECOND_PER_DAY = 24 * 60 * 60 * 1000;
    @Autowired
    private PersonRoleRepository personRoleRepository;

    private final Long MANAGER_ROLE = 2L;
    private final Long EMPLOYEE_ROLE = 3L;
    private final Long HR_ROLE = 1L;
    private final Long IT_SUPPORT_ROLE = 5L;

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest updateUserDto) throws Exception {
        try {
            ModelMapper modelMapper = new ModelMapper();
            Optional<Person> personFromDB = personRepository.findPersonByPersonId(updateUserDto.getPersonId());

            if (!personFromDB.isPresent()) {
                throw new Exception("Person not exist");
            }
            Person person = personFromDB.get();
            modelMapper.map(updateUserDto, person);
            Date dateOfBirth = new Date();
            dateOfBirth.setTime(person.getDateOfBirth().getTime() + MILLISECOND_PER_DAY); // go to the next day
            person.setDateOfBirth(dateOfBirth);
            personRepository.save(person);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        ResponseEntity<BaseResponse<HttpStatus, Void>> responseEntity
                = BaseResponse.ofSucceededOffset(HttpStatus.OK, null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager(String search) {
        List<Person> personList = personRepository.getMasterDataPersonByRole(CommonConstant.ROLE_ID_OF_MANAGER, search == null ? null : search.trim());
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
        if (employeeDetailDto != null) {
            EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null, employeeDetailDto);
            ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
                    .ofSucceeded(employeeListDtos);
            return responseEntity;
        } else {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName, String email, Long departmentId, String rollNumber, String status, Long positionId, String managerRoll) {
        page = page - 1;
        Pagination pagination = new Pagination(page, limit);
        Long managerId = null;
        if (!StringUtils.isEmpty(managerRoll)) {
            Optional<Person> personByRollNumber = personRepository.findPersonByRollNumberEquals(managerRoll);
            if (!personByRollNumber.isPresent()) {
                throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
            }
            Person person = personByRollNumber.get();
            managerRoll = person.getPersonId().toString();
            managerId = Long.parseLong(managerRoll);
        }
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email, departmentId, rollNumber, positionId, managerId, pagination);
        List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
        pagination.setTotalRecords(pageInfo);
        pagination.setPage(page + 1);
        ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(EmployeeUpdateRequest employeeRequest, String rollNumber) {
        EmployeeDetailDto employeeDetailDto = personRepository.getDetailEmployee(rollNumber);
        if (employeeDetailDto == null) {
            throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
        }
        if (StringUtils.isEmpty(employeeRequest.getFullName())) {
            employeeRequest.setFullName(employeeDetailDto.getFullName());
        }
        if (StringUtils.isEmpty(employeeRequest.getAddress())) {
            employeeRequest.setAddress(employeeDetailDto.getAddress());
        }
        if (StringUtils.isEmpty(employeeRequest.getCitizenIdentification())) {
            employeeRequest.setCitizenIdentification(employeeDetailDto.getCitizenIdentification());
        }
        if (StringUtils.isEmpty(employeeRequest.getPhoneNumber())) {
            employeeRequest.setPhoneNumber(employeeDetailDto.getPhoneNumber());
        }
        if (employeeRequest.getRankId() == null) {
            employeeRequest.setRankId(employeeDetailDto.getRankId());
        } else {
            Double annualLeaveBudget = convertAnnualLeaveBudget(employeeRequest.getRankId());
            personRepository.updateAnnualLeaveBudget(annualLeaveBudget, rollNumber);
        }
        if (employeeRequest.getDepartmentId() == null) {
            employeeRequest.setDepartmentId(employeeDetailDto.getDepartmentId());
        }
        if (employeeRequest.getManagerId() == null) {
            employeeRequest.setManagerId(employeeDetailDto.getManagerId());
        }
        if (employeeRequest.getGender() == null) {
            employeeRequest.setGender(employeeDetailDto.getGender());
        }
        if (employeeRequest.getPositionId() == null) {
            employeeRequest.setPositionId(employeeDetailDto.getPositionId());
        }
        if (employeeRequest.getSalaryBasic() == null) {
            employeeRequest.setSalaryBasic(Double.parseDouble(employeeDetailDto.getSalaryBasic()));
        }
        if (employeeRequest.getSalaryBonus() == null) {
            employeeRequest.setSalaryBonus(Double.parseDouble(employeeDetailDto.getSalaryBonus()));
        }
        if (employeeRequest.getIsManager() == null) {
            employeeRequest.setIsManager(employeeDetailDto.getIsManager());
        } else {
            updatePersonRole(employeeDetailDto, employeeRequest);
        }

        if (employeeRequest.getOnBoardDate() == null) {
            employeeRequest.setOnBoardDate(employeeDetailDto.getOnBoardDate().toString());
        }

        personRepository.updateEmployee(
                employeeRequest.getFullName(),
                employeeRequest.getManagerId(),
                employeeRequest.getDepartmentId(),
                employeeRequest.getPositionId(),
                employeeRequest.getRankId(),
                employeeRequest.getCitizenIdentification(),
                employeeRequest.getPhoneNumber(),
                employeeRequest.getAddress(),
                employeeRequest.getGender(),
                rollNumber,
                employeeRequest.getSalaryBasic(),
                employeeRequest.getSalaryBonus(),
                convertDateInput(employeeRequest.getOnBoardDate()));

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
        person.setAnnualLeaveBudget(convertAnnualLeaveBudget(employeeRequest.getRankId()));
        person.setSalaryBasic(employeeRequest.getSalaryBasic());
        person.setSalaryBonus(employeeRequest.getSalaryBonus());
        person.setDateOfBirth(convertDateInput(employeeRequest.getDateOfBirth()));
        person.setOnBoardDate(convertDateInput(employeeRequest.getOnBoardDate()));
        try {
            personRepository.save(person);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.newErrorCode(500, e.getMessage()));
        }
        EmployeeDetailDto personByRollNumber = personRepository.getDetailEmployee(person.getRollNumber());
        try {
            cretatePersonRole(personByRollNumber, employeeRequest);
        } catch (Exception e) {
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
    public ResponseEntity<BaseResponse<Boolean, Void>> checkPinCode(String pinCode) {
        Long personId = 2L;
        Optional<Person> person = personRepository.findById(personId);
        if (!person.isPresent()) {
            throw new BaseException(ErrorCode.NO_DATA);
        }
        if (person.get().getPinCode().equalsIgnoreCase(pinCode)) {
            return BaseResponse.ofSucceeded(true);
        }
        return BaseResponse.ofSucceeded(false);
    }

    @Override
    public List<EmployeeListDto> exportEmployee(String fullName, String email, Long departmentId, String rollNumber, Long positionId) {
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email, departmentId, rollNumber, positionId, null, null);
        List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
        return employeeListDtos;
    }

    private String convertRollNumber() {
        long count = personRepository.count() + 1;
        String rollNumber = "MS00" + count;
        return rollNumber;
    }

    private String convertMail(String fullName, String rollNumber) {
        String removeName = removeAccent(fullName);
        String[] split = removeName.split("\\s");
        rollNumber = rollNumber.substring(rollNumber.length() - 2, rollNumber.length());
        String fMailName = split[0];
        String lMailName = split[split.length - 1];
        return lMailName + "." + fMailName + rollNumber + "@minswap.com";
    }

    private Double convertAnnualLeaveBudget(Long rankId) {
        if (rankId == 1) {
            return 0.0;
        } else if (rankId == 2) {
            return 15.0;
        } else if (rankId == 3) {
            return 17.0;
        } else {
            return 20.0;
        }
    }

    public static String removeAccent(String s) {
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("");
    }

    private void cretatePersonRole(EmployeeDetailDto personByRollNumber, EmployeeRequest employeeRequest) {
        PersonRole personRole = new PersonRole();
        personRole.setRoleId(EMPLOYEE_ROLE);
        personRole.setPersonId(personByRollNumber.getPersonId());
        personRoleRepository.save(personRole);
        if (employeeRequest.getIsManager() == 1) {
            personRole.setRoleId(MANAGER_ROLE);
            personRoleRepository.save(personRole);
        }
        if (employeeRequest.getDepartmentId() == 1) {
            personRole.setRoleId(IT_SUPPORT_ROLE);
            personRoleRepository.save(personRole);
        } else if (employeeRequest.getDepartmentId() == 13) {
            personRole.setRoleId(HR_ROLE);
            personRoleRepository.save(personRole);
        }
    }

    private void updatePersonRole(EmployeeDetailDto employeeDetailDto, EmployeeUpdateRequest employeeRequest) {
        PersonRole personRole = new PersonRole();
        personRole.setPersonId(employeeDetailDto.getPersonId());
        if (employeeRequest.getIsManager() != null) {
            personRole.setRoleId(MANAGER_ROLE);
            if (employeeDetailDto.getIsManager() != null) {
                if (employeeRequest.getIsManager() == 0) {
                    personRoleRepository.delete(personRole);
                } else {
                    personRoleRepository.save(personRole);
                }
            }
        }
        if (employeeRequest.getDepartmentId() != null) {
            if (employeeRequest.getDepartmentId() == 1) {
                personRole.setRoleId(IT_SUPPORT_ROLE);
                personRoleRepository.save(personRole);
            } else if (employeeRequest.getDepartmentId() == 13) {
                personRole.setRoleId(HR_ROLE);
                personRoleRepository.save(personRole);
            } else {
                personRole.setRoleId(IT_SUPPORT_ROLE);
                personRoleRepository.delete(personRole);
                personRole.setRoleId(HR_ROLE);
                personRoleRepository.delete(personRole);
            }
        }
    }

    private Date convertDateInput(String dateStr) {
        try {
            SimpleDateFormat sm = new SimpleDateFormat("dd-MM-yyyy");
            Date date = sm.parse(dateStr);
            return date;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }
}
