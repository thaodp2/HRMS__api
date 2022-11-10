package com.minswap.hrms.service.person;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.entities.Department;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.request.UpdateUserRequest;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
