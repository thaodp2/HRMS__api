package com.minswap.hrms.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.response.EmployeeListResponse;
import com.minswap.hrms.response.dto.PersonDto;
import com.minswap.hrms.service.EmployeeHRService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmployeeHRServiceImpl implements EmployeeHRService {
	
	@Autowired
	PersonRepository personRepository;

	@Override
	public ResponseEntity<BaseResponse<EmployeeListResponse, Pageable>> getListEmployee(int page, int limit) {
		Pagination pagination = new Pagination(page, limit);
		Page<Person> pageInfo = personRepository.findAll(pagination);
		List<Person> people = pageInfo.getContent();
		List<PersonDto> peopleDto = people.stream().map(PersonDto::of).collect(Collectors.toList());
		pagination.setTotalRecords(pageInfo);
		ResponseEntity<BaseResponse<EmployeeListResponse, Pageable>> responseEntity = BaseResponse
				.ofSucceededOffset(EmployeeListResponse.of(peopleDto), pagination);
		return responseEntity;
	}

}
