package com.minswap.hrms.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class CommonFormatUtil.
 */
public class CommonFormatUtil {
  
  /**
   * Instantiates a new common format util.
   */
  private CommonFormatUtil() {
  
  }

  public static String formatCurrency(BigDecimal amount) {
    Locale defaultLocale = new Locale("en", "vn");
    DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(defaultLocale);
    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
    symbols.setCurrencySymbol(""); // Don't use null.
    formatter.setDecimalFormatSymbols(symbols);
    if (amount == null) {
      amount = BigDecimal.ZERO;
    }
    return formatter.format(amount.abs()).trim();
  }

  /**
   * Fomat string to date.
   *
   * @param input the input
   * @return the date
   * @throws ParseException the parse exception
   */
  public static Date fomatStringToDate(String input) throws ParseException {
    String pattern = "yyyyMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.parse(input);
  }

  /**
   * Fomat date to string.
   *
   * @param input the input
   * @return the string
   */
  public static String fomatDateToString(Date input) {
    String pattern = "yyyyMMdd";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    return simpleDateFormat.format(input);
  }

  /**
   * Now add day.
   *
   * @param numberDay the number day
   * @return the date
   */
  public static Date nowAddDay(int numberDay) {
    Calendar c = Calendar.getInstance();
    c.add(Calendar.DATE, numberDay);
    return c.getTime();
  }

  /**
   * Mapper json to obj.
   *
   * @param <T> the generic type
   * @param obj the obj
   * @param clazz the clazz
   * @return the t
   */
  public static <T> T mapperJsonToObj(Object obj, Class<T> clazz) {
    return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .convertValue(obj, clazz);
  }
  

  private static final String REGEX_FILTER_KEY =
      "[:]+((?=\\[)\\[[^]]*\\]|(?=\\{)\\{[^\\}]*\\}|\\\"[^\"]*\\\"|(\\d+(\\.\\d+)?))";

  static List<String> redactKeys =
      Collections.unmodifiableList(Arrays.asList("api_key", "api_secret", "identity_number", "email", "phone_number", "referral_code"));

  public static String redact(@NonNull String responseString) {
    for (String key : redactKeys) {
      Matcher matcher =
          Pattern.compile(String.format("\"%s\"%s", key, REGEX_FILTER_KEY)).matcher(responseString);
      if (matcher.find() && matcher.group(1) != null) {
        responseString = responseString.replace(matcher.group(1), "**********");
      }
    }
    return responseString;
  }


}
