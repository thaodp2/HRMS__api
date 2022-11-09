package com.minswap.hrms.exception.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.constants.ErrorCodeEnum;
import com.minswap.hrms.exception.annotation.ServiceProcessingValidateAnnotation;
import com.minswap.hrms.model.BaseResponse;
import com.minswap.hrms.model.BusinessCode;
import com.minswap.hrms.model.Meta;
import com.minswap.hrms.request.BasicRequest;

import net.logstash.logback.encoder.org.apache.commons.lang.math.NumberUtils;

/** The Class PaymentValidateAOPHandle. */
@Aspect
@Component
@Order(value = 3)
@PropertySource(value = "classpath:messages/message.properties", encoding = "UTF-8",
name = "messageCode")
public class ServiceProcessingValidateAOPHandle {

  /** The environment. */
  @Autowired private Environment environment;
  public static Map<String, String> dfsErrorList;
  /** The response format util. */
//  @Autowired private ResponseFormatUtil responseFormatUtil;

  @Autowired
  private Environment env;
  @Around("execution(* *(..)) && @annotation(hrmsValidateAnnotation)")
  public Object hrmsValidateAnnotation(
      ProceedingJoinPoint point, ServiceProcessingValidateAnnotation hrmsValidateAnnotation)
      throws Throwable {
    // Get dataRequest
    BasicRequest objectRequest = (BasicRequest) point.getArgs()[0];
    BindingResult bindingResult = (BindingResult) point.getArgs()[1];
    List<String> fieldDup = new ArrayList<>();
    AbstractEnvironment ae = (AbstractEnvironment) env;
    org.springframework.core.env.PropertySource dfsErrorSource =
            ae.getPropertySources().get("messageCode");
    Properties dfsProps = (Properties) dfsErrorSource.getSource();
    // Validate data
    if (bindingResult.hasErrors()) {
      BaseResponse responseBasicObj = new BaseResponse<>();
      responseBasicObj.setMetadata(Meta.buildMeta(ErrorCode.INVALID_PARAMETERS, null));
//              objectRequest.getRequestId(), ErrorCodeEnum.BAD_REQUEST.getValue(), null, null);
      bindingResult
          .getFieldErrors()
          .stream()
          .forEach(
              f -> {
                // Check field dupp
//                for (String checkDuppError : fieldDup) {
//                  if (checkDuppError.equals(f.getField())) {
//                    return;
//                  }
//                }
                fieldDup.add(f.getField());

                String code = f.getDefaultMessage();
                if (NumberUtils.isNumber(code)) {
	                BusinessCode businessCode = ErrorCode.newErrorCode(Integer.parseInt(code), dfsProps.get(code).toString(), HttpStatus.BAD_REQUEST);
	                responseBasicObj.setMetadata(Meta.buildMeta(businessCode, null));
                }
              });
      return formatResponse(responseBasicObj);
    } else {
      return point.proceed();
    }
  }
  public ResponseEntity<BaseResponse<String, Void>> formatResponse(BaseResponse responseBasicObj) {
    if (responseBasicObj.getMetadata().getCode() != ErrorCodeEnum.SUCCESS.getValue()) {
      responseBasicObj.ofFailed(Meta.buildMeta(ErrorCode.INTERNAL_SERVER_ERROR, null), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    return new ResponseEntity<BaseResponse<String, Void>>(responseBasicObj, HttpStatus.BAD_REQUEST);
  }
}
