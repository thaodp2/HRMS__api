package com.minswap.hrms.service.person;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.PersonRole;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.PersonRoleRepository;
import com.minswap.hrms.request.*;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.response.dto.MasterDataDto;
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

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.minswap.hrms.constants.ErrorCode.INVALID_FILE;
import static com.minswap.hrms.constants.ErrorCode.UPLOAD_EXCEL;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonRepository personRepository;

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

    private final Long MANAGER_ROLE = 2L;
    private final Long EMPLOYEE_ROLE = 3L;
    private final Long HR_ROLE = 1L;
    private final Long IT_SUPPORT_ROLE = 5L;

    HttpStatus httpStatus;

    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest updateUserDto, Long personId) throws Exception {

        Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(updateUserDto.getCitizenIdentification());
        Optional<Person> personFromDB = personRepository.findPersonByPersonId(personId);

        if (!personFromDB.isPresent()) {
            throw new Exception("Person not exist");
        }

        Person person = personFromDB.get();
        if (personCheckCitizen != null && personCheckCitizen > 0 && !person.getCitizenIdentification().equals(updateUserDto.getCitizenIdentification()) ) {
            throw new BaseException(ErrorCode.CITIZEN_INDENTIFICATION_EXSIT);
        }
        try {
            ModelMapper modelMapper = new ModelMapper();

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
    public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName, String email, Long departmentId, String rollNumber, String status, Long positionId, String managerRoll, String sort, String dir) {
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
        Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email, departmentId, rollNumber, positionId, managerId, PageRequest.of(page - 1, limit, dirSort == null ? Sort.unsorted() : Sort.by(dirSort, sort)));
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
        } else {
            Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(employeeRequest.getCitizenIdentification());
            if (personCheckCitizen != null && personCheckCitizen > 0) {
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
        if (employeeRequest.getActive() == null) {
            employeeRequest.setActive(employeeDetailDto.getStatus() + "");
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
                convertDateInput(employeeRequest.getOnBoardDate()),
                employeeRequest.getActive());

        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> createEmployee(EmployeeRequest employeeRequest) {
        Person person = new Person();
        person.setFullName(employeeRequest.getFullName());
        person.setAddress(employeeRequest.getAddress());
        Integer personCheckCitizen = personRepository.getUserByCitizenIdentification(employeeRequest.getCitizenIdentification());
        if (personCheckCitizen != null && personCheckCitizen > 0) {
            throw new BaseException(ErrorCode.CITIZEN_INDENTIFICATION_EXSIT);
        } else {
            person.setCitizenIdentification(employeeRequest.getCitizenIdentification());
        }
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
    public ResponseEntity<BaseResponse<Boolean, Void>> checkSecureCodeIsCorrect(UpdateSecureCodeRequest secureCodeRequest, Long personId) {

        Optional<Person> person = personRepository.findById(personId);
        if (!person.isPresent()) {
            throw new BaseException(ErrorCode.NO_DATA);
        }
        if (person.get().getPinCode().equalsIgnoreCase(secureCodeRequest.getCurrentSecureCode())) {
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
        if (person.get().getPinCode() == null) {
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
            return BaseResponse.ofSucceeded(false);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> updatePinCode(UpdateSecureCodeRequest secureCodeRequest, Long personId) {

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
            return BaseResponse.ofSucceeded(false);
        }
    }

    @Override
    public ResponseEntity<BaseResponse<Boolean, Void>> createPinCode(UpdateSecureCodeRequest secureCodeRequest, Long personId) {

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
            return BaseResponse.ofSucceeded(false);
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
    public boolean checkManagerIdValid(Long managerId) {
        Person p = personRepository.findById(managerId).orElse(null);
        if (p != null) {
            PersonRole pr = personRoleRepository.findByPersonIdAndAndRoleId(managerId, CommonConstant.ROLE_ID_OF_MANAGER).orElse(null);
            if (pr != null) {
                return true;
            }
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
    public ResponseEntity<BaseResponse<HttpStatus, Void>> importExcel(MultipartFile file) {
        int countRecordSuccess = 0;
        int countRecordFail = 0;
        String message = "";
        String rowFail = "";
        try {
            if (!file.isEmpty()) {
                if (file.getOriginalFilename().split("\\.")[1].equals("xlsx") || file.getOriginalFilename().split("\\.")[1].equals("xls")) {
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
                                    if ((row.getCell(0)== null) &&
                                            row.getCell(1)== null &&
                                            row.getCell(6)== null &&
                                            row.getCell(7)== null &&
                                            row.getCell(8)== null &&
                                            row.getCell(9)== null &&
                                            row.getCell(2)==null &&
                                            row.getCell(3)==null &&
                                            row.getCell(4)==null &&
                                            row.getCell(5)==null &&
                                            row.getCell(10)==null &&
                                            row.getCell(11)==null &&
                                            row.getCell(12)==null &&
                                            row.getCell(13)==null
                                    ) {
                                        continue;
                                    }
                                    try {
                                        String fullName = row.getCell(0).getStringCellValue();
                                        String dateOfBirth = row.getCell(1).getStringCellValue();
                                        String onBoardDate = row.getCell(6).getStringCellValue();
                                        String citizenIdentification = row.getCell(7).getStringCellValue();
                                        String phoneNumber = row.getCell(8).getStringCellValue();
                                        String address = row.getCell(9).getStringCellValue();
                                        long managerId = (long) row.getCell(2).getNumericCellValue();
                                        long departmentId = (long) row.getCell(3).getNumericCellValue();
                                        long positionId = (long) row.getCell(4).getNumericCellValue();
                                        long rankId = (long) row.getCell(5).getNumericCellValue();
                                        int gender = (int) row.getCell(10).getNumericCellValue();
                                        double salaryBasic = row.getCell(11).getNumericCellValue();
                                        double salaryBonus = row.getCell(12).getNumericCellValue();
                                        int isManager = (int) row.getCell(13).getNumericCellValue();

                                        if (checkManagerIdValid(managerId) &&
                                                departmentService.checkDepartmentExist(departmentId) &&
                                                positionService.checkPositionByDepartment(positionId, departmentId) &&
                                                rankService.checkRankExist(rankId) &&
                                                checkCCCDValid(citizenIdentification) &&
                                                checkPhoneValid(phoneNumber) &&
                                                checkGenderValid(gender) &&
                                                checkIsManagerValid(isManager)) {
                                            //create employee
                                            EmployeeRequest employeeRequest = new EmployeeRequest(fullName, dateOfBirth.toString(),
                                                    managerId, departmentId, positionId, rankId, onBoardDate.toString(),
                                                    citizenIdentification + "", phoneNumber + "",
                                                    address, gender, null, salaryBasic, salaryBonus, isManager
                                            );
                                            createEmployee(employeeRequest);
                                            countRecordSuccess++;
                                        } else {
                                            countRecordFail++;
                                            rowFail += (row.getRowNum() + 1) + ", ";
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
                            if (!rowFail.equals("")) {
                                message += "Create fail " + countRecordFail + " employee in rows (" + rowFail.substring(0, rowFail.length() - 2) + ")";
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
            SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sm.parse(dateStr);
            date.setTime(date.getTime() + MILLISECOND_PER_DAY);
            return date;
        } catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }
}
