package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.response.dto.LeaveBudgetDto;
import com.minswap.hrms.response.dto.MasterDataDto;
import lombok.Data;

import java.util.List;

@Data
public class MasterDataResponse {
    @JsonProperty(value = "items")
    private List<MasterDataDto> masterDataDtos;

    public MasterDataResponse(List<MasterDataDto> masterDataDtos) {
        this.masterDataDtos = masterDataDtos;
    }
}
