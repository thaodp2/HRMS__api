package com.minswap.hrms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.type.CollectionType;

public final class RequestResponseUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
//
//    public static HttpStatus detectHttpStatus(Error error) {
//        if (error.getHttpCode() != null) return HttpStatus.valueOf(error.getHttpCode());
//
//        String errorCode = String.valueOf(error.getCode());
//        String httpCode = errorCode.substring(0, 3);
//
//        return HttpStatus.valueOf(Integer.parseInt(httpCode));
//    }

//    public static void writeError(HttpServletResponse response, Error error) throws IOException {
//        response.setStatus(detectHttpStatus(error).value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//
//        MDCSnapshot.updateMDCErrorCode(error);
//        Meta meta = new Meta(error);
//        meta.setRequestId(MDC.get(RequestHeaders.X_REQUEST_ID));
//        BaseResponse baseResponse = new BaseResponse(meta);
//        response.getOutputStream().write(restResponseBytes(baseResponse));
//    }
//
//    private static byte[] restResponseBytes(Object res) throws JsonProcessingException {
//        String serialized = mapper.writeValueAsString(res);
//        return serialized.getBytes();
//    }

    public static <T> T getDataObject(JsonNode responseBody, Class<T> type) throws JsonProcessingException {
        if (responseBody != null && responseBody.hasNonNull("data")) {
            JsonNode dataObj = responseBody.get("data");
            return mapper.treeToValue(dataObj, type);
        } else {
            return null;
        }
    }

    public static <T> List<T> getDataArray(JsonNode responseBody, Class<T> type) throws IOException {
        if (responseBody != null && responseBody.hasNonNull("data")) {
            JsonNode dataObject = responseBody.get("data");
            CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, type);
            ObjectReader reader = mapper.readerFor(collectionType);
            return reader.readValue(dataObject);
        } else {
            return new ArrayList<>();
        }
    }

}
