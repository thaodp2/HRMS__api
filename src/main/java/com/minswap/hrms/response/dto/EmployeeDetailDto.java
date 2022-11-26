package com.minswap.hrms.response.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;
import javax.validation.constraints.Min;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDto {

    @JsonProperty("id")
    private Long personId;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("dateOfBirth")
    private Date dateOfBirth;

    @JsonProperty("gender")
    private int gender;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("citizenIdentification")
    private String citizenIdentification;

    @JsonProperty("address")
    private String address;

    @JsonProperty("rollNumber")
    private String rollNumber;

    @JsonProperty("email")
    private String email;

    @JsonProperty("departmentId")
    private Long departmentId;

    @JsonProperty("positionId")
    private Long positionId;

    @JsonProperty("rankId")
    private Long rankId;

    @JsonProperty("onBoardDate")
    private Date onBoardDate;

    @JsonProperty("isActive")
    private String status;

    @JsonProperty("managerId")
    private Long managerId;

    @Column(name = "avatarImg")
    private String avatarImg;

    @Column(name = "salaryBasic")
    private Double salaryBasic;

    @Column(name = "salaryBonus")
    private Double salaryBonus;

    @JsonProperty("isManager")
    private Integer isManager;

    public EmployeeDetailDto(Long personId, String fullName, Date dateOfBirth, int gender, String phoneNumber, String citizenIdentification, String address, String rollNumber, String email, Long departmentId, Long positionId, Long rankId, Date onBoardDate, String status, Long managerId, String avatarImg, Double salaryBasic, Double salaryBonus) {
        this.personId = personId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.citizenIdentification = citizenIdentification;
        this.address = address;
        this.rollNumber = rollNumber;
        this.email = email;
        this.departmentId = departmentId;
        this.positionId = positionId;
        this.rankId = rankId;
        this.onBoardDate = onBoardDate;
        this.status = status;
        this.managerId = managerId;
        this.avatarImg = avatarImg;
        this.salaryBasic = salaryBasic;
        this.salaryBonus = salaryBonus;
    }

    public int getStatus() {
    	return status == null ? 0 : Integer.parseInt(status);
    }
    public String getSalaryBasic() {
        return salaryBasic == null ? "" : converDouble(salaryBasic);
    }
    public String getSalaryBonus() {
        return salaryBonus == null ? "" : converDouble(salaryBonus);
    }
    private String converDouble(Double salary){
        BigDecimal bigDecimal = new BigDecimal(salary);// form to BigDecimal
        return bigDecimal.toString();
    }

}
