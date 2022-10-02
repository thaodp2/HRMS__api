package com.minswap.hrms.util;

import java.util.UUID;

public class UUIDUtil {
  public UUIDUtil() {}

  public static String generateUUID() {
    return toBase62(UUID.randomUUID().toString().replaceAll("-", ""));
  }

  private static String toBase62(String hex) {
    StringBuilder uri = new StringBuilder(BaseConvert.convert(hex, 16, 62));

    while (uri.length() < 22) {
      uri.insert(0, '0');
    }

    return uri.toString();
  }
}
