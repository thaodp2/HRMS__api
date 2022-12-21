package com.minswap.hrms.service.signatureProfile;

import com.minswap.hrms.constants.CommonConstant;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.entities.Person;
import com.minswap.hrms.entities.SignatureProfile;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.exception.model.Pagination;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.repsotories.PersonRepository;
import com.minswap.hrms.repsotories.SignatureProfileRepository;
import com.minswap.hrms.request.SignatureProfileRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.minswap.hrms.response.MasterDataResponse;
import com.minswap.hrms.response.SignatureMasterDataResponse;
import com.minswap.hrms.response.SignatureProfileResponse;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.response.dto.SignatureMasterDataDto;
import com.minswap.hrms.response.dto.SignatureProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class SignatureProfileServiceImpl implements SignatureProfileService{
    @Autowired
    private SignatureProfileRepository signatureProfileRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    public ResponseEntity<BaseResponse<Void, Void>> updateSignatureRegister(SignatureProfileRequest signatureProfileRequest) {
        signatureProfileRepository.findSignatureProfileByRegisteredDate(convertDateInput(signatureProfileRequest.getRegisteredDate()))
                .ifPresent(signatureProfile -> {
                    if (signatureProfile.getPersonId() != -1) {
                        throw new BaseException(ErrorCode.newErrorCode(HttpStatus.NOT_FOUND.value(), "Signature is registered"));
                    }
                    signatureProfile.setPersonId(Long.parseLong(signatureProfileRequest.getPersonId()));
                    signatureProfileRepository.save(signatureProfile);
                });
        return BaseResponse.ofSucceeded(null);
    }
    private Date convertDateInput(String dateStr){
        try{
            SimpleDateFormat sm = new SimpleDateFormat(CommonConstant.YYYY_MM_DD_HH_MM_SS);
            Date date = sm.parse(dateStr);
            date.setTime(date.getTime());
            return date;
        }catch (Exception e) {
            throw new BaseException(ErrorCode.DATE_FAIL_FOMART);
        }
    }
    @Override
    public ResponseEntity<BaseResponse<Void, Void>> deleteSignatureRegister(SignatureProfileRequest signatureProfileRequest) {
        signatureProfileRepository.findSignatureProfileByRegisteredDate(
                convertDateInput(signatureProfileRequest.getRegisteredDate())
                ).filter(sp -> sp.getPersonId() == Long.parseLong(signatureProfileRequest.getPersonId()))
                .ifPresent(signatureProfileRepository::delete);
        return BaseResponse.ofSucceeded(null);
    }

    @Override
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(Integer isRegistered, String search, int page,int limit) {
        List<SignatureProfile> list = signatureProfileRepository.findAll(Sort.by(Sort.Direction.DESC, "registeredDate"));
        list = list.stream()
                .filter(sp -> (sp.getPersonId() != -1))
                .collect(Collectors.toList());
        List<SignatureProfileDto> items = list.stream()
                .map(this::SignatureProfileToDTO)
                .collect(Collectors.toList());
        if (search != null) {
            items = items.stream()
                    .filter(sp -> sp.getEmployeeName().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        Pagination pagination = new Pagination(page, limit, list.size());
        return BaseResponse.ofSucceededOffset(new SignatureProfileResponse(items), pagination);
    }

    @Override
    public ResponseEntity<BaseResponse<SignatureMasterDataResponse, Pageable>> getMasterDataSignature(String search) {
        List<SignatureProfile> listSignatureProfile = new ArrayList<>();
        if (search != null) {
            Optional<SignatureProfile> signatureProfileOptional =
                    signatureProfileRepository.findSignatureProfileByRegisteredDate(convertDateInput(search));
            SignatureProfile signatureProfile = signatureProfileOptional.get();
            if (signatureProfile != null) {
                listSignatureProfile.add(signatureProfile);
            }
        }
        else {
            listSignatureProfile = signatureProfileRepository.findSignatureProfilesByPersonId(Long.valueOf(-1));
        }
        List<SignatureMasterDataDto> signatureMasterDataDtos = new ArrayList<>();
        for (SignatureProfile signatureProfile : listSignatureProfile) {
            Date registerDate = signatureProfile.getRegisteredDate();
            registerDate.setTime(registerDate.getTime() - CommonConstant.MILLISECOND_7_HOURS);
            SignatureMasterDataDto signatureMasterDataDto = new SignatureMasterDataDto(registerDate + "",
                                                                                       registerDate + "");
            signatureMasterDataDtos.add(signatureMasterDataDto);
        }
        SignatureMasterDataResponse response = new SignatureMasterDataResponse(signatureMasterDataDtos);
        ResponseEntity<BaseResponse<SignatureMasterDataResponse, Pageable>> responseEntity
                = BaseResponse.ofSucceededOffset(response, null);
        return responseEntity;
    }

    private SignatureProfileDto SignatureProfileToDTO(SignatureProfile signatureProfile) {
        Optional<Person> personByPersonId = personRepository.findPersonByPersonId(signatureProfile.getPersonId());
        return personByPersonId.map(person -> new SignatureProfileDto(
                1,
                signatureProfile.getPersonId(),
                person.getFullName(),
                person.getRollNumber(),
                signatureProfile.getRegisteredDate())
        ).orElse(new SignatureProfileDto(
                0,
                signatureProfile.getPersonId(),
                "",
                "",
                signatureProfile.getRegisteredDate())
        );
    }

}
