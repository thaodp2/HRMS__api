package com.minswap.hrms.response.dto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class PositionDto {

    public PositionDto(Long positionId, String positionName, int isAllowDelete) {
        this.positionId = positionId;
        this.positionName = positionName;
        this.isAllowDelete = isAllowDelete;
    }

    public PositionDto(String positionName) {
        this.positionName = positionName;
    }

    private Long positionId;

    private String positionName;

    private int isAllowDelete;
}
