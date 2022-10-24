package com.minswap.hrms.service.impl;

import java.util.List;

import com.minswap.hrms.response.dto.EmployeeDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getListEmployee(int page, int limit) {
		page = page - 1;
		Pagination pagination = new Pagination(page, limit);
		Page<EmployeeListDto> pageInfo = personRepository.getListPerson(pagination);
		List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
		pagination.setTotalRecords(pageInfo);
		pagination.setPage(page + 1);
		ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
				.ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
		return responseEntity;
	}

	@Override
	public ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> getDetailEmployee(Long personID) {
		EmployeeDetailDto employeeListDto = personRepository.getDetailEmployee(personID);
		EmployeeInfoResponse employeeListDtos = new EmployeeInfoResponse(null,employeeListDto);
		ResponseEntity<BaseResponse<EmployeeInfoResponse, Void>> responseEntity = BaseResponse
				.ofSucceeded(employeeListDtos);

		return responseEntity;
	}

	@Override
	public ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> getSearchListEmployee(int page, int limit, String fullName, String email, String departmentName, String rollNumber, String status, String positionName) {
		Pagination pagination = new Pagination(page, limit);
		Page<EmployeeListDto> pageInfo = personRepository.getSearchListPerson(fullName,email,departmentName,rollNumber,status,positionName,pagination);
		List<EmployeeListDto> employeeListDtos = pageInfo.getContent();
		pagination.setTotalRecords(pageInfo);
		ResponseEntity<BaseResponse<EmployeeInfoResponse, Pageable>> responseEntity = BaseResponse
				.ofSucceededOffset(EmployeeInfoResponse.of(employeeListDtos), pagination);
		return responseEntity;
	}

	@Override
	public ResponseEntity<BaseResponse<Void, Void>> changeStatusEmployee(Long personId, String active) {
		personRepository.updateStatusEmployee(active, personId);

		ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
		return responseEntity;
	}

}
