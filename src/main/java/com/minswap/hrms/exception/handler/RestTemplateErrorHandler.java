package com.minswap.hrms.exception.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.minswap.hrms.constants.ErrorCode;
import com.minswap.hrms.exception.model.BaseException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RestTemplateErrorHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        HttpStatus status = httpResponse.getStatusCode();
        return (status.is4xxClientError() || status.is5xxServerError());
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        InputStream error = httpResponse.getBody();
        String errorMessage = this.getErrorMessage(error).toString();
        log.error(errorMessage);
        throw new BaseException(ErrorCode.newErrorCode(errorMessage));
    }

    private StringBuilder getErrorMessage(InputStream error) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (error, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                builder.append((char) c);
            }
        }
        return builder;
    }
}
