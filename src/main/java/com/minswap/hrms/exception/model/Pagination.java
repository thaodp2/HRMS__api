package com.minswap.hrms.exception.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Pagination {
	  @JsonProperty("page")
	  private int page;
	  
	  @JsonProperty("limit")
	  private int limit;
  
	  @JsonProperty("totalRecords")
	  private int totalRecords;

}
