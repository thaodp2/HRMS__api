package com.minswap.hrms.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * The Class RequestBasicObj.
 */
@JsonInclude(Include.NON_NULL)
@Data
public class RequestDto implements Serializable {

  private static final long serialVersionUID = 7541979383555784522L;

  @JsonProperty("request_id")
  private String requestId;

  @JsonProperty("uri")
  private String uri;

  /** The api key. */
  @JsonProperty("api_key")
  private String apiKey;

  /** The api secret. */
  @JsonProperty("api_secret")
  private String apiSecret;

}
