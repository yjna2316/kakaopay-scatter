package com.github.kakaopayserver.controller;

import org.springframework.http.HttpStatus;

public class ApiError {

  private final String code;
  private final String message;
  private final int status;

  ApiError(ErrorCode errorCode, HttpStatus status) {
    this.code = errorCode.getCode();
    this.message = errorCode.getMessage();
    this.status = status.value();
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public int getStatus() {
    return status;
  }
}
