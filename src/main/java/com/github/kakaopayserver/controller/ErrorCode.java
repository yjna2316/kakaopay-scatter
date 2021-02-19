package com.github.kakaopayserver.controller;

public enum ErrorCode {
  // COMMON
  INTERNAL_SERVER_ERROR("C001", "알 수 없는 에러가 발생하였습니다."),
  BAD_REQUEST("C002", "잘못된 요청입니다."),
  NOT_FOUND("C003", "요청하신 내용을 찾을 수 없습니다."),

  MONEY_CATTER_FINISHED("SC001", "이미 종료되었습니다."),
  MONEY_SCATTER_ALREADY_RECEIVED("SC002", "이미 받은 이력이 있습니다."),
  MONEY_SCATTER_SAME_USER("SC003", "본인이 만든 뿌린기는 받을 수 없습니다."),
  MONEY_SCATTER_SAME_ROOM("SC004", "유효하지 않은 대화방입니다."),
  MONEY_SCATTER_EXPIRED_TOKEN("SC005", "만료된 토큰입니다."),
  MONEY_SCATTER_INVALID_USER("SC006", "본인이 만든 뿌린기만 확인할 수 있습니다.");

  private final String code;
  private final String message;

  ErrorCode(final String code, final String message) {
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}