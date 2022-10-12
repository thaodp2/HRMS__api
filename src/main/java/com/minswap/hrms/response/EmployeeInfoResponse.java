package com.minswap.hrms.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.exception.model.Pagination;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInfoResponse {
  
  @JsonProperty("personId")
  private int personId;
  
  @JsonProperty("fullName")
  private String fullName;

  @JsonProperty("address")
  private String address;
  
  @JsonProperty("citizen_identification")
  private String citizen_identification;
  
  @JsonProperty("phone_number")
  private String phone_number;
  
  @JsonProperty("email")
  private String email;
  
  @JsonProperty("on_board_date")
  private String on_board_date;
  
  @JsonProperty("rank_id")
  private String rank_id;
  
  @JsonProperty("department_id")
  private String department_id;
  
  @JsonProperty("manager_id")
  private String manager_id;
  
  @JsonProperty("gender")
  private String gender;
  
  @JsonProperty("roll_number")
  private String roll_number;
  
  @JsonProperty("date_of_birth")
  private String date_of_birth;
  
  @JsonProperty("status")
  private String status;

}
