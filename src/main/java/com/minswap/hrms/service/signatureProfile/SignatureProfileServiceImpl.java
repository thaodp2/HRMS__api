package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.request.SignatureProfileRequest;

import java.util.List;
import java.util.Optional;

import com.minswap.hrms.response.EmployeeInfoResponse;
import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.SignatureProfileResponse;
import com.minswap.hrms.response.dto.EmployeeDetailDto;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignatureProfileServiceImpl implements SignatureProfileService{
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private SignatureProfileRepository signatureProfileRepository;

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateSignatureRegister(SignatureProfileRequest signatureProfileRequest) {
        return null;
    }

    @Override
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(int isRegistered, String sort, String dir, int page,int limit) {
        List<SignatureProfile> list = signatureProfileRepository.findByRegistered(isRegistered, sort, dir);
        return null;
    }

    @Override
    public ResponseEntity<BaseResponse<MasterDataResponse, Pageable>> getMasterDataAllEmployee(String search) {
        return null;
    }
}
