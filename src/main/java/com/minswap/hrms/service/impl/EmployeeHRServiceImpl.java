package com.minswap.hrms.service.impl;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.request.EmployeeRequest;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.dto.EmployeeListDto;
import com.minswap.hrms.service.EmployeeHRService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeHRServiceImpl implements EmployeeHRService {
	
	@Autowired
	PersonRepository personRepository;
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
		}catch (Exception e) {
			throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
		}

		EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null,employeeDetailDto);
		ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
				.ofSucceeded(employeeListDtos);

		return responseEntity;
	}

	@Override
	public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName,String email,String departmentName,String rollNumber,String status,String positionName, String managerRoll) {
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
		Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName, email,departmentName,rollNumber,positionName,managerId,pagination);
		List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
		pagination.setTotalRecords(pageInfo);
		pagination.setPage(page + 1);
		ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
				.ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
		return responseEntity;
	}

	@Override
	public ResponseEntity<BaseResponse<Void, Void>> changeStatusEmployee(String rollNumber, String active) {
		personRepository.updateStatusEmployee(active, rollNumber);

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
	public ResponseEntity<BaseResponse<Void, Void>> exportEmployee() {
		return null;
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
