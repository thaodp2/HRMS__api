package com.minswap.hrms.filter;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by phucnguyen on 09/03/2017.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

	/** The logger. */
	private Logger logger = LoggerFactory.getLogger(CorsFilter.class);

	@SuppressWarnings("unused")
	@Autowired
	private Environment env;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 * javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) res;
		HttpServletRequest request = (HttpServletRequest) req;
		try {
			ApiKeyVerifiRequestWrapper requestWrapper = new ApiKeyVerifiRequestWrapper(request);
			JSONParser parser = new JSONParser();
			JSONObject dataRequest = StringUtils.isEmpty(requestWrapper.getBody()) ? new JSONObject()
					: (JSONObject) parser.parse(requestWrapper.getBody());
			requestWrapper.setBody(dataRequest.toString());
//			response.setHeader("Access-Control-Allow-Origin", "*");
//			response.setHeader("Access-Control-Allow-Credentials", "true");
//			response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
//			response.setHeader("Access-Control-Max-Age", "3600");
//			response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers");

			chain.doFilter(requestWrapper, res);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

}
