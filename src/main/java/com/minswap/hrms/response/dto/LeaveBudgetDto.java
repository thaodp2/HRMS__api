package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import javax.persistence.Column;
import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder("id")
public class LeaveBudgetDto {

    public LeaveBudgetDto(Long leaveBudgetId, String fullName, Double leaveBudget, Double numberOfDayOff, Double remainDayOff) {
        this.leaveBudgetId = leaveBudgetId;
        this.fullName = fullName;
        this.leaveBudget = leaveBudget;
        this.numberOfDayOff = numberOfDayOff;
        this.remainDayOff = remainDayOff;
    }

    public LeaveBudgetDto(double leaveBudget,
                          double numberOfDayOff,
                          double remainDayOff) {
        this.leaveBudget = leaveBudget;
        this.numberOfDayOff = numberOfDayOff;
        this.remainDayOff = remainDayOff;
    }

    @JsonProperty("id")
    private Long leaveBudgetId;

    private String fullName;

    private Double leaveBudget;

    private Double numberOfDayOff;

    private Double remainDayOff;

    private String requestTypeName;
}
