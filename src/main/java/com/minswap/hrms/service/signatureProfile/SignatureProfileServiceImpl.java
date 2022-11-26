package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.request.SignatureProfileRequest;

import java.util.Optional;

import com.minswap.hrms.response.dto.EmployeeDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignatureProfileServiceImpl implements SignatureProfileService{
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private SignatureProfileRepository signatureProfileRepository;
    @Override
    public ResponseEntity<BaseResponse<Void, Void>> registerSignature(SignatureProfileRequest signatureProfileRequest) {
        SignatureProfile signatureProfile = new SignatureProfile();
        signatureProfile.setRollNumber(signatureProfileRequest.getRollNumber());
        signatureProfile.setPrivateKeySignature(signatureProfileRequest.getIdSignature());
        EmployeeDetailDto employeeDetailDto = personRepository.getDetailEmployee(signatureProfile.getRollNumber());
        if(employeeDetailDto == null){
            throw new BaseException(ErrorCode.PERSON_NOT_EXIST);
        }
        signatureProfile.setPersonId(employeeDetailDto.getPersonId());
        signatureProfileRepository.save(signatureProfile);
        ResponseEntity<BaseResponse<Void, Void>> responseEntity = BaseResponse.ofSucceeded(null);
        return responseEntity;
    }
}
