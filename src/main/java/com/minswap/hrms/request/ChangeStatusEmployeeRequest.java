package com.minswap.hrms.request;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeStatusEmployeeRequest extends BasicRequest{

   @JsonProperty("isActive")
   @NotNull(message = "608")
   @NotEmpty(message = "608")
   private String active;

}
