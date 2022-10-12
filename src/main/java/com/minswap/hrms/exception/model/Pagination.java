package com.minswap.hrms.exception.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties({"sort", "offset", "pageSize","pageNumber", "paged", "unpaged"})
public class Pagination extends PageRequest {
	
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Pagination(int page, int size) {
		super(page, size, Sort.unsorted());
		this.page = page;
		this.limit = size;
	}

	@JsonProperty("page")
	  private int page;
	  
	  @JsonProperty("limit")
	  private int limit;
  
	  @JsonProperty("totalRecords")
	  private long totalRecords;
	  
	  public void setTotalRecords(Page pageInfo) {
		  this.totalRecords = pageInfo.getTotalElements();
	  }
}
