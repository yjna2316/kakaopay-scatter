package com.github.kakaopayserver.util;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.UUID;

public class StringTokenUtils {

  /*
  * 랜덤 문자열 토큰 생성
  * 알파벳과 숫자로 이루어진 count만큼 랜덤으로 뽑는다.
  **/
  public static String generate(int count) {
    return RandomStringUtils.randomAlphanumeric(count);
  }
}
