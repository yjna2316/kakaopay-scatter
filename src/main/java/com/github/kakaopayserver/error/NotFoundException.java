package com.github.kakaopayserver.error;

import com.github.kakaopayserver.controller.ErrorCode;

public class NotFoundException extends ServiceRuntimeException {
  public NotFoundException(ErrorCode error) {
    super(error);
  }
}
