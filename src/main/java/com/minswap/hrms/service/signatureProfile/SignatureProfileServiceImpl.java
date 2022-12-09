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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.minswap.hrms.response.SignatureProfileResponse;
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
        signatureProfileRepository.deleteAll(signatureProfileRepository.findSignatureProfilesByPersonId(Long.parseLong(signatureProfileRequest.getPersonId())));
        return BaseResponse.ofSucceeded(null);
    }

    @Override
    public ResponseEntity<BaseResponse<SignatureProfileResponse, Pageable>> listSignatureRegister(Integer isRegistered, String search, int page,int limit) {
        List<SignatureProfile> list = signatureProfileRepository.findAll(Sort.by(Sort.Direction.DESC, "registeredDate"));
        if (isRegistered != null) {
            list = list.stream()
                    .filter(sp -> (isRegistered == 1) == (sp.getPersonId() != -1))
                    .collect(Collectors.toList());
        }
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

    private SignatureProfileDto SignatureProfileToDTO(SignatureProfile signatureProfile) {
        Optional<Person> personByPersonId = personRepository.findPersonByPersonId(signatureProfile.getPersonId());
        return personByPersonId.map(person -> new SignatureProfileDto(
                1,
                signatureProfile.getPersonId(),
                person.getFullName(),
                signatureProfile.getRegisteredDate())
        ).orElse(new SignatureProfileDto(
                0,
                signatureProfile.getPersonId(),
                "",
                signatureProfile.getRegisteredDate())
        );
    }

}
