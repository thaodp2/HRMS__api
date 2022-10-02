package com.minswap.hrms.util;


import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.reflection.MetaObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.constants.ErrorCodeEnum;
import com.minswap.hrms.exception.model.BaseException;
import com.minswap.hrms.filter.ApiKeyVerifiRequestWrapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtil {
    private CommonUtil() {
    }

    public static HttpServletRequest getCurrentHttpRequest() {
    	Optional<HttpServletRequest> httpRequestOpt = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(ServletRequestAttributes.class::isInstance)
                .map(ServletRequestAttributes.class::cast)
                .map(ServletRequestAttributes::getRequest);
        if (!httpRequestOpt.isPresent()) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return httpRequestOpt.get();
    }

    public static String objectToString(Object object) {
        if (object == null) {
            return "";
        }
        return object.toString();
    }
    public static String getRequestId(HttpServletRequest req) {
      HttpServletRequest request = (HttpServletRequest) req;
      try {
          ApiKeyVerifiRequestWrapper requestWrapper = new ApiKeyVerifiRequestWrapper(request);
          JSONParser parser = new JSONParser();
          JSONObject dataRequest = StringUtils.isEmpty(requestWrapper.getBody()) ? new JSONObject()
                  : (JSONObject) parser.parse(requestWrapper.getBody());

          Object reqId = dataRequest.get("request_id");
//          return Objects.isNull(reqId) ? "" : (String) reqId;
          return (String) reqId;
      } catch (Exception e) {
          return "";
      }
  }

    public static String jsonObjectToString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static String encodeBasicAuthentication(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }


}
