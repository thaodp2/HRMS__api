package com.minswap.hrms.service.impl;

import com.minswap.hrms.entities.Person;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    PersonRepository personRepository;
    @Override
    public ResponseEntity<BaseResponse<HttpStatus, Void>> updateUserInformation(UpdateUserRequest updateUserDto) throws Exception{
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
}
