package com.minswap.hrms.util;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class JsonUtil.
 */
@Component
public class JsonUtil {
	private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);

	private ObjectMapper objectMapper;

	@PostConstruct
	private void init() {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	/**
	 * Instantiates a new json util.
	 */
	private JsonUtil() {

	}

	/**
	 * Gets the generic object.
	 *
	 * @param <T>   the generic type
	 * @param input the input
	 * @param clazz the clazz
	 * @return the generic object
	 */
	public <T> T getEntityFromJsonObj(Object input, Class<T> clazz) {
		return objectMapper.convertValue(input, clazz);
	}
	
	public <T> T getEntityFromJsonStr(String input, Class<T> clazz) {
	    try {
	      return objectMapper.readValue(input, clazz);
	    } catch (IOException e) {
	      return null;
	    }
	  }

	/**
	 * Gets the generic object.
	 *
	 * @param <T>   the generic type
	 * @param input the input
	 * @param clazz the clazz
	 * @return the generic object
	 */

	public static <T> T getGenericObject(Object input, Class<T> clazz) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.convertValue(input, clazz);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}

	/**
	 *
	 * Gets the generic object from string
	 *
	 * @param <T>   the generic type
	 * @param input the input
	 * @param clazz the clazz
	 * @return the generic object
	 */
	public static <T> T getGenericObject(String input, Class<T> clazz) throws IOException {
		if(input == null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper.readValue(input, clazz);
	}

	public static <T> T getGenericStringList(String input, TypeReference<T> typeRef)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return objectMapper.readValue(input, typeRef);
	}

	public static String toJsonString(Object input) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return objectMapper.writeValueAsString(input);
		} catch (Exception e) {
		}
		return "";
	}
}
