package com.minswap.hrms.response.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class PositionDto {

    public PositionDto(int isAllowDelete, Long positionId, String positionName) {
        this.isAllowDelete = isAllowDelete;
        this.positionId = positionId;
        this.positionName = positionName;
    }

    private int isAllowDelete;
    private Long positionId;
    private String positionName;


}
