package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.request.SignatureProfileRequest;

import java.util.List;
import java.util.stream.Collectors;

import com.minswap.hrms.response.SignatureProfileResponse;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignatureProfileServiceImpl implements SignatureProfileService{
    @Autowired
    private SignatureProfileRepository signatureProfileRepository;

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateSignatureRegister(SignatureProfileRequest signatureProfileRequest) {
        signatureProfileRepository.findSignatureProfileByPrivateKeySignature(signatureProfileRequest.getPrivateKeySignature())
                .ifPresent(signatureProfile -> {
                    signatureProfile.setPersonId(Long.parseLong(signatureProfileRequest.getPersonId()));
                    signatureProfileRepository.save(signatureProfile);
                });
        return BaseResponse.ofSucceeded(null);
    }

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> deleteSignatureRegister(SignatureProfileRequest signatureProfileRequest) {
        signatureProfileRepository.deleteAll(signatureProfileRepository.findSignatureProfilesByPersonId(Long.parseLong(signatureProfileRequest.getPersonId())));
        return BaseResponse.ofSucceeded(null);
    }

    @Override
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(Integer isRegistered, int page,int limit) {
        List<SignatureProfile> list = signatureProfileRepository.findAll(Sort.by(Sort.Direction.DESC, "registeredDate"));
        List<SignatureProfileDto> items = list.stream()
                .filter(sp -> (isRegistered == 1) == (sp.getPersonId() != -1))
                .map(this::SignatureProfileToDTO)
                .collect(Collectors.toList());
        Pagination pagination = new Pagination(page, limit, list.size());
        return BaseResponse.ofSucceededOffset(new SignatureProfileResponse(items), pagination);
    }

    private SignatureProfileDto SignatureProfileToDTO(SignatureProfile signatureProfile) {
        return new SignatureProfileDto(signatureProfile.getPrivateKeySignature(), signatureProfile.getPersonId(), signatureProfile.getRegisteredDate());
    }

}
