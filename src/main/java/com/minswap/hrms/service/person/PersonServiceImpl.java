package com.minswap.hrms.service.person;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.LeaveBudget;
import com.minswap.hrms.entities.OTBudget;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.PersonRole;
import com.minswap.hrms.entities.Role;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.repsotories.LeaveBudgetRepository;
import com.minswap.hrms.repsotories.OTBudgetRepository;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.request.*;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.ListRolesResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.response.dto.RequestDto;
import com.minswap.hrms.security.UserPrincipal;
import com.minswap.hrms.service.email.EmailSenderService;
import com.minswap.hrms.util.CommonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import com.minswap.hrms.service.department.DepartmentService;
import com.minswap.hrms.service.position.PositionService;
import com.minswap.hrms.service.rank.RankService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.minswap.hrms.constants.ErrorCode.*;
import static org.mockito.ArgumentMatchers.charThat;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    EntityManager entityManager;
    @Autowired
    private PersonRepository personRepository;

    private static final long MILLISECOND_PER_8H = 8 * 60 * 60 * 1000;

    private static final long MILLISECOND_PER_DAY = 24 * 60 * 60 * 1000;

    @Autowired
    private PersonRoleRepository personRoleRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    DepartmentService departmentService;

    @Autowired
    PositionService positionService;

    @Autowired
    RankService rankService;

    @Autowired
    LeaveBudgetRepository leaveBudgetRepository;

    @Autowired
    OTBudgetRepository otBudgetRepository;

    private final Long MANAGER_ROLE = 2L;
    private final Long EMPLOYEE_ROLE = 3L;
    private final Long HR_ROLE = 1L;
    private final Long IT_SUPPORT_ROLE = 5L;

    HttpStatus httpStatus;

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> updateUserInformation(UpdateUserRequest updateUserDto, Long personId) throws Exception {

        Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(updateUserDto.getCitizenIdentification());
        Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);

        if (!personFromDB.isPresent()) {
            throw new Exception("Person not exist");
        }

        Person person = personFromDB.get();
        if (personCheckCitizen != null && personCheckCitizen > 0 && !person.getCitizenIdentification().equals(updateUserDto.getCitizenIdentification())) {
            throw new BaseException(ErrorCode.CITIZEN_INDENTIFICATION_EXSIT);
        }
        if (updateUserDto.getAvatarImg() == null) {
            updateUserDto.setAvatarImg(person.getAvatarImg());
        }
        try {
            ModelMapper modelMapper = new ModelMapper();
            modelMapper.map(updateUserDto, person);
            Date dateOfBirth = new Date();
            dateOfBirth.setTime(person.getDateOfBirth().getTime() + MILLISECOND_PER_8H); // go to the next day
            person.setDateOfBirth(dateOfBirth);
            personRepository.save(person);
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
        EmployeeDetailDto employeeDetailDto = personRepository.getDetailEmployee(person.getRollNumber());
        EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null, employeeDetailDto);
        ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
                .ofSucceeded(employeeListDtos);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllManager(Long departmentId, String rollNumber, String search) {
        List<Person> personList = null;
        if (rollNumber != null && !rollNumber.isEmpty()) {
            //List<Person> personList = personRepository.getMasterDataManagerByDepartment(CommonConstant.ROLE_ID_OF_MANAGER, search == null ? null : search.trim(), departmentId);
            personList = getMasterDataManagerToEdit(departmentId, rollNumber, search == null ? null : search.trim());
        } else {
            personList = personRepository.getMasterDataManagerToCreate(CommonConstant.ROLE_ID_OF_MANAGER, search == null ? null : search.trim(), departmentId);
        }

        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        if (personList != null && !personList.isEmpty()) {
            for (int i = 0; i < personList.size(); i++) {
                MasterDataDto masterDataDto = new MasterDataDto(personList.get(i).getFullName() + " - " + personList.get(i).getRollNumber(), personList.get(i).getPersonId());
                masterDataDtos.add(masterDataDto);
            }
        }
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;

    }


    public List<Person> getMasterDataManagerToEdit(Long departmentId, String rollNumber, String search) {
        List<Person> personList = null;
        try {
            HashMap<String, Object> params = new HashMap<>();
            Person person = personRepository.findPersonByRollNumberEquals(rollNumber).orElse(null);
            if (person != null) {
                StringBuilder queryAllRequest = new StringBuilder("WITH RECURSIVE Subordinates AS " +
                        "(" +
                        "SELECT e.person_id , e.full_name , e.manager_id " +
                        "FROM person AS e " +
                        "WHERE e.manager_id =:personId " +
                        "UNION ALL " +
                        "SELECT e.person_id , e.full_name , e.manager_id " +
                        "FROM person AS e " +
                        "INNER JOIN Subordinates AS sub ON e.manager_id = sub.person_id " +
                        ") " +
                        "select p.person_id  as personId, p.full_name  as fullName, p.roll_number as rollNumber " +
                        "from person p, person_role pr, role r " +
                        "where p.person_id = pr.person_id and pr.role_id  = r.role_id " +
                        "and r.role_id = :roleId " +
                        "AND (:search IS NULL OR (p.roll_number like :search or p.full_name like :search)) " +
                        "AND (:departmentId IS NULL OR p.department_id =:departmentId) " +
                        "and p.person_id not in(" +
                        "SELECT DISTINCT s.person_id " +
                        "FROM Subordinates AS s) " +
                        "and p.person_id not in (" +
                        "select p.person_id from person p where p.roll_number =:rollNumber)");
                params.put("personId", person.getPersonId());
                params.put("roleId", CommonConstant.ROLE_ID_OF_MANAGER);
                if (search != null) {
                    params.put("search", "%" + search + "%");
                } else {
                    params.put("search", search);

                }
                params.put("departmentId", departmentId);
                params.put("rollNumber", rollNumber);

                Session session = entityManager.unwrap(Session.class);

                Query query = session.createNativeQuery(queryAllRequest.toString())
                        .addScalar("personId", LongType.INSTANCE)
                        .addScalar("fullName", StringType.INSTANCE)
                        .addScalar("rollNumber", StringType.INSTANCE)
                        .setResultTransformer(Transformers.aliasToBean(Person.class));

                params.forEach(query::setParameter);
                personList = query.getResultList();
            }
        } catch (Exception e) {
            return null;
        }
        return personList;
    }

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(String rollNumber) {
        EmployeeDetailDto employeeDetailDto = personRepository.getDetailEmployee(rollNumber);
        PersonRole pr = personRoleRepository.findByPersonIdAndAndRoleId(employeeDetailDto.getPersonId(), CommonConstant.ROLE_ID_OF_MANAGER).orElse(null);
        if (pr != null) {
            employeeDetailDto.setIsManager(1);
        }
        if (employeeDetailDto != null) {
            employeeDetailDto.setUserName(getUserName(employeeDetailDto.getEmail()));
            EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null, employeeDetailDto);
            ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
                    .ofSucceeded(employeeListDtos);
            return responseEntity;
        } else {
            throw new BaseException(ErrorCode.RESULT_NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page,
                                                                                              int limit, String fullName, String email, Long departmentId, String rollNumber, String status, Long
                                                                                                      positionId, String managerRoll, String sort, String dir) {
        if (StringUtils.isEmpty(sort) && StringUtils.isEmpty(dir)) {
            sort = "personId";
            dir = "DESC";
        }
        Sort.Direction dirSort = CommonUtil.getSortDirection(sort, dir);
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
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson((fullName == null || fullName.trim().isEmpty()) ? null : fullName.trim(), (email == null || email.trim().isEmpty()) ? null : email.trim(), departmentId, (rollNumber == null || rollNumber.trim().isEmpty()) ? null : rollNumber.trim(), positionId, managerId, status == null ? null : status, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
        List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
        for (EmployeeListDto employeeListDto : employeeListDtos) {
            employeeListDto.setUserName(getUserName(employeeListDto.getEmail()));
        }
        pagination.setTotalRecords(pageInfo);
        ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
                .ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getEmployeeMasterData(String search) {
        List<Person> people;
        people = personRepository.findAll();
        List<MasterDataDto> masterDataDtos = new ArrayList<>();
        if (search != null) {
            people = people.stream().filter(person -> person.getFullName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
        }
        people.forEach(person -> {
            MasterDataDto masterDataDto = new MasterDataDto(person.getFullName() + " - " + person.getRollNumber(), person.getPersonId());
            masterDataDtos.add(masterDataDto);
        });
        MasterDataResponse response = new MasterDataResponse(masterDataDtos);
        return BaseResponse.ofSucceededOffset(response, null);
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateEmployee(EmployeeUpdateRequest employeeRequest, String
            rollNumber) {
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
        } else {
            Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(employeeRequest.getCitizenIdentification());
            if (personCheckCitizen != null && personCheckCitizen > 0 && !employeeDetailDto.getCitizenIdentification().equals(employeeRequest.getCitizenIdentification())) {
                throw new BaseException(ErrorCode.CITIZEN_INDENTIFICATION_EXSIT);
            } else {
                employeeRequest.setCitizenIdentification(employeeRequest.getCitizenIdentification());
            }
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
            employeeRequest.setManagerId(null);
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
        //update role person 
        updatePersonRole(employeeDetailDto, employeeRequest);
        if (employeeRequest.getOnBoardDate() == null) {
            employeeRequest.setOnBoardDate(employeeDetailDto.getOnBoardDate().toString());
        }
        if (employeeRequest.getActive() == null) {
            employeeRequest.setActive(employeeDetailDto.getStatus() + "");
        }
        personRepository.updateEmployee(
                employeeRequest.getFullName().trim(),
                employeeRequest.getManagerId(),
                employeeRequest.getDepartmentId(),
                employeeRequest.getPositionId(),
                employeeRequest.getRankId(),
                employeeRequest.getCitizenIdentification(),
                employeeRequest.getPhoneNumber().trim(),
                employeeRequest.getAddress().trim(),
                employeeRequest.getGender(),
                rollNumber,
                employeeRequest.getSalaryBasic(),
                employeeRequest.getSalaryBonus(),
                convertDateInput(employeeRequest.getOnBoardDate()),
                employeeRequest.getActive());

        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest) {
        Person person = new Person();
        person.setFullName(employeeRequest.getFullName().trim());
        person.setAddress(employeeRequest.getAddress().trim());
        Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(employeeRequest.getCitizenIdentification());
        if (personCheckCitizen != null && personCheckCitizen > 0) {
            throw new BaseException(ErrorCode.CITIZEN_INDENTIFICATION_EXSIT);
        } else {
            person.setCitizenIdentification(employeeRequest.getCitizenIdentification());
        }
        person.setPhoneNumber(employeeRequest.getPhoneNumber().trim());
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
        //update leave budget
        if (person.getRankId() != 1) {
            List<LeaveBudget> leaveBudgetList = new ArrayList<>();
            leaveBudgetList.add(new LeaveBudget(person.getPersonId(), person.getAnnualLeaveBudget() / 12, 0, person.getAnnualLeaveBudget() / 12, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[0]));
            leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 180, 0, 180, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[1]));
            leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 20, 0, 20, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[2]));
            leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 70, 0, 70, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[3]));
            leaveBudgetList.add(new LeaveBudget(person.getPersonId(), 3, 0, 3, Year.now(), CommonConstant.LIST_REQUEST_TYPE_ID_IN_LEAVE_BUDGET[4]));
            try {
                leaveBudgetRepository.saveAll(leaveBudgetList);
            } catch (Exception e) {
                throw new BaseException(ErrorCode.newErrorCode(500, e.getMessage()));
            }
            List<OTBudget> otBudgetList = new ArrayList<>();
            otBudgetList.add(new OTBudget(person.getPersonId(), 40, 0, 40, 200, java.time.LocalDateTime.now().getMonthValue(), Year.now()));
            otBudgetRepository.saveAll(otBudgetList);
        }
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateStatusEmployee(ChangeStatusEmployeeRequest
                                                                                 employeeRequest, String rollNumber) {
        personRepository.updateStatusEmployee(employeeRequest.getActive(),
                rollNumber);

        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> checkSecureCodeIsCorrect(UpdateSecureCodeRequest
                                                                                        secureCodeRequest, Long personId) {

        Optional<Person> person = personRepository.findById(personId);
        if (!person.isPresent()) {
            throw new BaseException(ErrorCode.NO_DATA);
        }
        String pinCode = person.get().getPinCode() == null ? "" : person.get().getPinCode();
        if (pinCode.equalsIgnoreCase(secureCodeRequest.getCurrentSecureCode())) {
            return BaseResponse.ofSucceeded(true);
        }
        return BaseResponse.ofSucceeded(false);
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> checkSecureCodeIsExist(Long personId) {

        Optional<Person> person = personRepository.findById(personId);
        if (!person.isPresent()) {
            throw new BaseException(ErrorCode.NO_DATA);
        }
        if (person.get().getPinCode() == null || person.get().getPinCode().equals("")) {
            return BaseResponse.ofSucceeded(false);
        }
        return BaseResponse.ofSucceeded(true);
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> forgotPinCode(Long personId) {

        try {

            Optional<Person> optionalPerson = personRepository.findById(personId);
            if (!optionalPerson.isPresent()) {
                throw new BaseException(ErrorCode.newErrorCode(404,
                        "Person not found!",
                        httpStatus.NOT_FOUND));
            }
            String generatedString = RandomStringUtils.randomAlphanumeric(8);
            Person person = optionalPerson.get();
            person.setPinCode(generatedString);
            personRepository.save(person);
            String body = "Your pin code has been updated : " + generatedString;

            emailSenderService.sendEmail(person.getEmail(), "Update New Pin Code", body);
            return BaseResponse.ofSucceeded(true);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.SEND_PIN_CODE_FAILED);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> updatePinCode(UpdateSecureCodeRequest
                                                                             secureCodeRequest, Long personId) {

        Optional<Person> optionalPerson = personRepository.findById(personId);
        if (!optionalPerson.isPresent()) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Person not found!",
                    httpStatus.NOT_FOUND));
        }
        Person person = optionalPerson.get();
        if (!secureCodeRequest.getCurrentSecureCode().equalsIgnoreCase(person.getPinCode())) {
            throw new BaseException(ErrorCode.CURRENT_SECURE_CODE_INCORRECT);
        }
        if (!secureCodeRequest.getNewSecureCode().equals(secureCodeRequest.getConfirmSecureCode())) {
            throw new BaseException(ErrorCode.SECURE_CODE_AND_CONFIRM_CODE_DO_NOT_MATCH);
        }
        if (secureCodeRequest.getCurrentSecureCode().equalsIgnoreCase(secureCodeRequest.getNewSecureCode())) {
            throw new BaseException(ErrorCode.NEW_CODE_AND_CURRENT_CODE_MUST_DIFFERENT);

        }

        try {
            person.setPinCode(secureCodeRequest.getNewSecureCode());
            personRepository.save(person);

            return BaseResponse.ofSucceeded(true);
        } catch (Exception e) {
            throw new BaseException(UPDATE_FAIL);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> createPinCode(UpdateSecureCodeRequest
                                                                             secureCodeRequest, Long personId) {

        Optional<Person> optionalPerson = personRepository.findById(personId);
        if (!optionalPerson.isPresent()) {
            throw new BaseException(ErrorCode.newErrorCode(404,
                    "Person not found!",
                    httpStatus.NOT_FOUND));
        }
        Person person = optionalPerson.get();

        if (!secureCodeRequest.getNewSecureCode().equals(secureCodeRequest.getConfirmSecureCode())) {
            throw new BaseException(ErrorCode.SECURE_CODE_AND_CONFIRM_CODE_DO_NOT_MATCH);
        }

        try {
            person.setPinCode(secureCodeRequest.getNewSecureCode());
            personRepository.save(person);

            return BaseResponse.ofSucceeded(true);
        } catch (Exception e) {
            throw new BaseException(CREATE_FAIL);
        }
    }

    @Override
    public boolean isValidHeaderTemplate(Row row) {
        try {
            for (int i = 0; i < CommonConstant.TEMPLATE_HEADER_TO_IMPORT.length; i++) {
                if (!row.getCell(i).getStringCellValue().equals(CommonConstant.TEMPLATE_HEADER_TO_IMPORT[i])) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkManagerByDepartmentValid(Long managerId, Long departmentId) {
        if (managerId != null) {
            Person p = personRepository.findById(managerId).orElse(null);
            if (p != null) {
                PersonRole pr = personRoleRepository.findByPersonIdAndAndRoleId(managerId, CommonConstant.ROLE_ID_OF_MANAGER).orElse(null);
                if (pr != null && p.getDepartmentId() == departmentId) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean checkManagerToEdit(Long departmentId, String rollNumber, Long managerId) {
        if (managerId != null) {
            List<Person> personList = getMasterDataManagerToEdit(departmentId, rollNumber, null);
            if (personList != null && !personList.isEmpty()) {
                for (Person person : personList) {
                    if (person.getPersonId() == managerId) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
        return false;
    }

    public boolean checkManagerToCreate(Long departmentId, Long managerId) {
        if (managerId != null) {
            List<Person> personList = personRepository.getMasterDataManagerToCreate(CommonConstant.ROLE_ID_OF_MANAGER,null, departmentId);
            if (personList != null && !personList.isEmpty()) {
                for (Person person : personList) {
                    if (person.getPersonId() == managerId) {
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkGenderValid(Integer gender) {
        if (gender == 0 || gender == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIsManagerValid(Integer isManager) {
        if (isManager == 0 || isManager == 1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkPhoneValid(String phone) {
        return phone.matches("^(0?)(3[2-9]|5[6|8|9]|7[0|6-9]|8[0-6|8|9]|9[0-4|6-9])[0-9]{7}$");
    }

    @Override
    public boolean checkCCCDValid(String cccd) {
        return cccd.matches("^[0-9]{9}$|^[0-9]{12}$");
    }

    @Override
    public boolean checkSalaryValid(Double salary) {
        if (salary >= 0) {
            return true;
        }
        return false;
    }

    public boolean checkFormatDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(CommonConstant.YYYY_MM_DD);
            Date date1 = format.parse(date);
            return true;
        } catch (Exception x) {
            return false;
        }
    }

    public boolean checkIsActive(String isActive) {
        if (isActive.equals("0") || isActive.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(MultipartFile file) {
        int countRecordSuccess = 0;
        int countRecordUpdateSuccess = 0;
        int countRecordFail = 0;
        String message = "";
        String rowFail = "";


        try {
            if (!file.isEmpty()) {
                if (file.getOriginalFilename().split("\\.")[1].equals("xlsx")) {
                    try {
                        Path tempDir = Files.createTempDirectory("");
                        File tempFile = tempDir.resolve(file.getOriginalFilename()).toFile();
                        file.transferTo(tempFile);
                        Workbook workbook = WorkbookFactory.create(tempFile);
                        Sheet sheet = workbook.getSheetAt(0);
                        int rowStart = 1;
                        if (isValidHeaderTemplate(sheet.getRow(0))) {
                            for (Row row : sheet) {
                                if (rowStart != 1) {
                                    if ((row.getCell(0) == null) &&
                                            row.getCell(1) == null &&
                                            //row.getCell(6) == null &&
                                            row.getCell(7) == null &&
                                            //row.getCell(8) == null &&
                                            row.getCell(9) == null &&
                                            row.getCell(2) == null &&
                                            row.getCell(3) == null &&
                                            //row.getCell(4) == null &&
                                            row.getCell(5) == null &&
                                            row.getCell(10) == null &&
                                            row.getCell(11) == null &&
                                            row.getCell(12) == null &&
                                            row.getCell(13) == null &&
                                            //row.getCell(14) == null &&
                                            row.getCell(15) == null &&
                                            row.getCell(16) == null &&
                                            row.getCell(17) == null &&
                                            //row.getCell(18) == null &&
                                            row.getCell(19) == null
                                            //&&
                                            //row.getCell(20) == null
                                    ) {
                                        continue;
                                    }
                                    try {
                                        Long managerId = null;
                                        Long departmentId = null;
                                        Long positionId = null;
                                        Long rankId = null;
                                        Integer gender = null;
                                        Double salaryBasic = null;
                                        Double salaryBonus = null;
                                        Integer isManager = null;
                                        String fullName = null;
                                        String dateOfBirth = null;
                                        String onBoardDate = null;
                                        String citizenIdentification = null;
                                        String phoneNumber = null;
                                        String address = null;
                                        String isActive = null;
                                        String rollNumber = null;

                                        if (row.getCell(0) != null) {
                                            rollNumber = row.getCell(0).getStringCellValue();
                                        }
                                        if (row.getCell(1) != null) {
                                            fullName = row.getCell(1).getStringCellValue();
                                        }
                                        if (row.getCell(2) != null) {
                                            dateOfBirth = row.getCell(2).getStringCellValue();
                                        }
                                        if (row.getCell(9) != null) {
                                            onBoardDate = row.getCell(9).getStringCellValue();
                                        }
                                        if (row.getCell(10) != null) {
                                            citizenIdentification = row.getCell(10).getStringCellValue();
                                        }
                                        if (row.getCell(11) != null) {
                                            phoneNumber = row.getCell(11).getStringCellValue();
                                        }
                                        if (row.getCell(12) != null) {
                                            address = row.getCell(12).getStringCellValue();
                                        }
                                        if (row.getCell(20) != null) {
                                            isActive = row.getCell(20).getStringCellValue();
                                            if (isActive.equals("")) {
                                                isActive = null;
                                            }
                                        }

                                        if (row.getCell(3) != null) {
                                            Person person = personRepository.findPersonByRollNumberEquals(row.getCell(3).getStringCellValue()).orElse(null);
                                            if(person != null){
                                                managerId = person.getPersonId();
                                            }
                                            //managerId = (long) row.getCell(3).getNumericCellValue();
                                        }
                                        if (row.getCell(4) != null) {
                                            departmentId = (long) row.getCell(4).getNumericCellValue();
                                        }

                                        if (row.getCell(6) != null) {
                                            positionId = (long) row.getCell(6).getNumericCellValue();
                                        }

                                        if (row.getCell(8) != null) {
                                            rankId = (long) row.getCell(8).getNumericCellValue();
                                        }

                                        if (row.getCell(14) != null) {
                                            gender = (int) row.getCell(14).getNumericCellValue();
                                        }

                                        if (row.getCell(18) != null) {
                                            isManager = (int) row.getCell(18).getNumericCellValue();
                                        }

                                        if (row.getCell(15) != null) {
                                            salaryBasic = row.getCell(15).getNumericCellValue();
                                        }
                                        if (row.getCell(16) != null) {
                                            salaryBonus =row.getCell(16).getNumericCellValue();
                                        }

                                        if (rollNumber != null && !rollNumber.trim().isEmpty()) {
                                            //update
                                            Person person = personRepository.findPersonByRollNumberEquals(rollNumber.trim()).orElse(null);
                                            if (person != null) {
                                                if (fullName == null || fullName.trim().isEmpty()
                                                        || dateOfBirth == null || dateOfBirth.trim().isEmpty()
                                                        || !checkFormatDate(dateOfBirth) ||
                                                        !checkManagerToEdit(departmentId,rollNumber,managerId)
                                                        //!checkManagerByDepartmentValid(managerId, departmentId)
                                                        || !departmentService.checkDepartmentExist(departmentId)
                                                        || !positionService.checkPositionByDepartment(positionId, departmentId)
                                                        || !rankService.checkRankExist(rankId)
                                                        || !checkFormatDate(onBoardDate)
                                                        || !checkCCCDValid(citizenIdentification)
                                                        || !checkPhoneValid(phoneNumber)
                                                        || !checkGenderValid(gender)
                                                        || !checkIsManagerValid(isManager)
                                                        || !checkSalaryValid(salaryBasic)
                                                        || !checkSalaryValid(salaryBonus)
                                                        || !checkIsActive(isActive)) {
                                                    throw new BaseException(INVALID_PARAMETERS);
                                                } else {
                                                    EmployeeUpdateRequest employeeUpdateRequest = new EmployeeUpdateRequest(fullName, dateOfBirth, managerId, departmentId
                                                            , positionId, rankId, onBoardDate, citizenIdentification, phoneNumber, address, gender, isActive, salaryBasic, salaryBonus, isManager);
                                                    updateEmployee(employeeUpdateRequest, rollNumber.trim());
                                                    countRecordUpdateSuccess++;
                                                }
                                            }else {
                                                throw new BaseException(INVALID_PARAMETERS);
                                            }
                                        } else {
                                            //create employee
                                            if (fullName == null || fullName.trim().isEmpty()
                                                    || dateOfBirth == null || dateOfBirth.trim().isEmpty()
                                                    || !checkFormatDate(dateOfBirth) ||
                                                    !checkManagerToCreate(departmentId,managerId)
                                                    //!checkManagerByDepartmentValid(managerId, departmentId)
                                                    || !departmentService.checkDepartmentExist(departmentId)
                                                    || !positionService.checkPositionByDepartment(positionId, departmentId)
                                                    || !rankService.checkRankExist(rankId)
                                                    || !checkFormatDate(onBoardDate)
                                                    || !checkCCCDValid(citizenIdentification)
                                                    || !checkPhoneValid(phoneNumber)
                                                    || !checkGenderValid(gender)
                                                    || !checkIsManagerValid(isManager)
                                                    || !checkSalaryValid(salaryBasic)
                                                    || !checkSalaryValid(salaryBonus)) {
                                                throw new BaseException(INVALID_PARAMETERS);
                                            } else {
                                                EmployeeRequest employeeRequest = new EmployeeRequest(fullName, dateOfBirth,
                                                        managerId, departmentId, positionId, rankId, onBoardDate,
                                                        citizenIdentification, phoneNumber,
                                                        address, gender, null, salaryBasic, salaryBonus, isManager
                                                );
                                                createEmployee(employeeRequest);
                                                countRecordSuccess++;
                                            }
                                        }
                                    } catch (Exception e) {
                                        //show dòng bị fail
                                        countRecordFail++;
                                        rowFail += (row.getRowNum() + 1) + ", ";
                                    }
                                }
                                rowStart++;
                            }
                            //show number sucess
                            message += "Create success " + countRecordSuccess + " employee. ";
                            message += "Update success " + countRecordUpdateSuccess + " employee. ";
                            if (!rowFail.equals("")) {
                                message += "Create and update fail " + countRecordFail + " employee in rows (" + rowFail.substring(0, rowFail.length() - 2) + ")";
                            }
                            return BaseResponse.ofSucceededOffset(HttpStatus.OK, null, message);
                        } else {
                            return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                        }
                    } catch (Exception ex) {
                        return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                    }
                } else {
                    return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
                }
            } else {
                return BaseResponse.ofFailedCustom(Meta.buildMeta(UPLOAD_EXCEL, null), null);
            }
        } catch (Exception e) {
            return BaseResponse.ofFailedCustom(Meta.buildMeta(INVALID_FILE, null), null);
        }
    }

    @Override
    public Person getPersonInforByEmail(String email) {
        Person person = personRepository.findPersonByEmail(email).orElse(null);
        return person;
    }

    @Override
    public ResponseEntity<BaseResponse<ListRolesResponse, Void>> getRoles(UserPrincipal userPrincipal) {
        List<Role> roles = userPrincipal.getRoles();
        return roles == null
                ? BaseResponse.ofFailedCustom(Meta.buildMeta(UNAUTHORIZE, null), null)
                : BaseResponse.ofSucceeded(new ListRolesResponse(roles));
    }

    @Override
    public List<EmployeeListDto> exportEmployee(String fullName, String email, Long departmentId, String
            rollNumber, Long positionId) {
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email, departmentId, rollNumber, positionId, null, null, null);
        List<EmployeeListDto> employeeListDtos = null;
        if (pageInfo != null) {
            employeeListDtos = pageInfo.getContent();
        }
        return employeeListDtos;
    }

    private String convertRollNumber() {
        long count = personRepository.count() + 1;
        String rollNumber = "MS";
        int length = (count + "").length();
        for (int i = 0; i < 5 - length; i++) {
            rollNumber += "0";
        }
        rollNumber += count;
        return rollNumber;
    }

    private String convertMail(String fullName, String rollNumber) {
        String removeName = removeAccent(fullName);
        String[] split = removeName.split("\\s");
        String fMailName = split[split.length - 1];
        for (String string : split) {
            fMailName += string.charAt(0);
        }
        fMailName = fMailName.substring(0, fMailName.length() - 1);
        Integer countPersonByMail = personRepository.getCountPersonByMail(fMailName);
        return fMailName + countPersonByMail + "@minswap.com";
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
            PersonRole personRole1 = new PersonRole();
            personRole1.setRoleId(MANAGER_ROLE);
            personRole1.setPersonId(personByRollNumber.getPersonId());
            personRoleRepository.save(personRole1);
        }
        if (employeeRequest.getDepartmentId() == 35) {
            PersonRole personRole2 = new PersonRole();
            personRole2.setRoleId(IT_SUPPORT_ROLE);
            personRole2.setPersonId(personByRollNumber.getPersonId());
            personRoleRepository.save(personRole2);
        } else if (employeeRequest.getDepartmentId() == 2) {
            PersonRole personRole3 = new PersonRole();
            personRole3.setPersonId(personByRollNumber.getPersonId());
            personRole3.setRoleId(HR_ROLE);
            personRoleRepository.save(personRole3);
        }
    }

    private void updatePersonRole(EmployeeDetailDto employeeDetailDto, EmployeeUpdateRequest employeeRequest) {
        PersonRole personRole = new PersonRole();
        personRole.setPersonId(employeeDetailDto.getPersonId());
        PersonRole pr = personRoleRepository.findByPersonIdAndAndRoleId(employeeDetailDto.getPersonId(), CommonConstant.ROLE_ID_OF_MANAGER).orElse(null);
        if (employeeRequest.getIsManager() != null) {
            personRole.setRoleId(MANAGER_ROLE);
            if (pr != null) {
                if (employeeRequest.getIsManager() == 0) {
                    personRoleRepository.delete(pr);
                }
            } else {
                personRoleRepository.save(personRole);
            }
        } else {
            if (pr != null) {
                personRoleRepository.delete(pr);
            }
        }
        if (employeeRequest.getDepartmentId() != null) {
            PersonRole prItSp = personRoleRepository.findByPersonIdAndAndRoleId(employeeDetailDto.getPersonId(), CommonConstant.ROLE_ID_OF_IT_SUPPORT).orElse(null);
            PersonRole prHr = personRoleRepository.findByPersonIdAndAndRoleId(employeeDetailDto.getPersonId(), HR_ROLE).orElse(null);

            if (employeeRequest.getDepartmentId() == 35 && prItSp == null) {
                PersonRole personRole1 = new PersonRole();
                personRole1.setPersonId(employeeDetailDto.getPersonId());
                personRole1.setRoleId(IT_SUPPORT_ROLE);
                personRoleRepository.save(personRole1);
                if (prHr != null) {
                    personRoleRepository.delete(prHr);
                }
            } else if (employeeRequest.getDepartmentId() == 2 && prHr == null) {
                PersonRole personRole2 = new PersonRole();
                personRole2.setPersonId(employeeDetailDto.getPersonId());
                personRole2.setRoleId(HR_ROLE);
                personRoleRepository.save(personRole2);
                if (prItSp != null) {
                    personRoleRepository.delete(prItSp);
                }
            }
        }
    }

    private Date convertDateInput(String dateStr) {
        try {
            SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sm.parse(dateStr);
            date.setTime(date.getTime() + MILLISECOND_PER_DAY);
            return date;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }

    private String getUserName(String gmail) {
        String[] userNameArr = gmail.split("@");
        return userNameArr[0];
    }
}
