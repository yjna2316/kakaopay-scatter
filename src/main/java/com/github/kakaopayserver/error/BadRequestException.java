package com.github.kakaopayserver.error;

import com.github.kakaopayserver.controller.ErrorCode;

public class BadRequestException extends ServiceRuntimeException {
  public BadRequestException(ErrorCode error) {
    super(error);
  }
}
