package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.MasterDataDto;
import com.minswap.hrms.response.dto.SignatureMasterDataDto;
import lombok.Data;

import java.util.List;

@Data
public class SignatureMasterDataResponse {
    @JsonProperty(value = "items")
    private List<SignatureMasterDataDto> signatureMasterDataDtos;

    public SignatureMasterDataResponse(List<SignatureMasterDataDto> signatureMasterDataDtos) {
        this.signatureMasterDataDtos = signatureMasterDataDtos;
    }
}
