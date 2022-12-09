package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minswap.hrms.constants.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignatureProfileDto {

    private String privateKeySignature;

    private Long personId;

    @JsonFormat(pattern = CommonConstant.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private Date registeredDate;
}
