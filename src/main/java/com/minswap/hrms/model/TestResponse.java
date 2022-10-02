package com.minswap.hrms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.minswap.hrms.exception.model.Pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestResponse {
  
  @JsonProperty("test_account")
  private String testAccount;
  
  @JsonProperty("request_id")
  private String requestId;

  public String getTestAccount() {
    return testAccount;
  }

  public void setTestAccount(String testAccount) {
    this.testAccount = testAccount;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

}
