package com.minswap.hrms.response.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.entities.Person;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
  
  @JsonProperty("personId")
  private int personId;
  
  @JsonProperty("fullName")
  private String fullName;

  @JsonProperty("address")
  private String address;
  
  @JsonProperty("citizenIdentification")
  private String citizenIdentification;
  
  @JsonProperty("phoneNumber")
  private String phoneNumber;
  
  @JsonProperty("email")
  private String email;
  
  @JsonProperty("onBoardDate")
  private String onBoardDate;
  
  @JsonProperty("rankId")
  private String rankId;
  
  @JsonProperty("departmentId")
  private String departmentId;
  
  @JsonProperty("managerId")
  private String managerId;
  
  @JsonProperty("gender")
  private String gender;
  
  @JsonProperty("rollNumber")
  private String rollNumber;
  
  @JsonProperty("dateOfBirth")
  private String dateOfBirth;
  
  @JsonProperty("status")
  private String status;

  public static PersonDto of (Person person) {
	  return PersonDto.builder()
			  .personId(person.getPersonId())
			  .fullName(person.getFullName())
			  .address(person.getAddress())
			  .citizenIdentification(person.getCitizenIdentification())
			  .phoneNumber(person.getPhoneNumber())
			  .email(person.getEmail())
			  .onBoardDate(person.getOnBoardDate())
			  .rankId(person.getRankId())
			  .departmentId(person.getDepartmentId())
			  .managerId(person.getManagerId())
			  .gender(person.getGender())
			  .rollNumber(person.getRollNumber())
			  .dateOfBirth(person.getDateOfBirth())
			  .status(person.getStatus())
			  .build();
  }
}
