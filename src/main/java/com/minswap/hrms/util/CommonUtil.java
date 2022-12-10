package com.minswap.hrms.util;


import java.util.Base64;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.google.gson.Gson;
import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.model.BaseException;
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

    public static String jsonObjectToString(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    public static String encodeBasicAuthentication(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }

    public static Sort.Direction getSortDirection(String sort, String dir){
        Sort.Direction dirSort = null;
        if (sort != null && !sort.trim().isEmpty()) {
            if (dir == null || dir.trim().equalsIgnoreCase("asc")) {
                dirSort = Sort.Direction.ASC;
            } else if (dir.trim().equalsIgnoreCase("desc")) {
                dirSort = Sort.Direction.DESC;
            }
        }
        return dirSort;
    }


}
