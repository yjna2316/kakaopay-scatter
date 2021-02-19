package com.github.kakaopayserver.error;

import com.github.kakaopayserver.controller.ErrorCode;

public class ServiceRuntimeException extends RuntimeException {

  private final ErrorCode error;

  public ServiceRuntimeException(ErrorCode error) {
    this.error = error;
  }

  public ErrorCode getError() {
    return this.error;
  }
}
