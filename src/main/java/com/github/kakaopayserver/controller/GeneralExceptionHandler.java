package com.github.kakaopayserver.controller;

import com.github.kakaopayserver.error.BadRequestException;
import com.github.kakaopayserver.error.NotFoundException;
import com.github.kakaopayserver.error.ServiceRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.github.kakaopayserver.controller.ApiResult.ERROR;

@ControllerAdvice
public class GeneralExceptionHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private ResponseEntity<ApiResult<?>> newResponse(ErrorCode errorCode, HttpStatus status) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    return new ResponseEntity<>(ERROR(errorCode, status), headers, status);
  }

  @ExceptionHandler(ServiceRuntimeException.class)
  public ResponseEntity<?> handleServiceRuntimeException(ServiceRuntimeException e) {
    if (e instanceof NotFoundException)
      return newResponse(e.getError(), HttpStatus.NOT_FOUND);
    if (e instanceof BadRequestException)
      return newResponse(e.getError(), HttpStatus.BAD_REQUEST);

    log.warn("Unexpected service exception occurred: {}", e.getMessage(), e);
    return newResponse(e.getError(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({Exception.class, RuntimeException.class})
  public ResponseEntity<?> handleException(Exception e) {
    log.error("Unexpected exception occurred: {}", e.getMessage(), e);
    return newResponse(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
