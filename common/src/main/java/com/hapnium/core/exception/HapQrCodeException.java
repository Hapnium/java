package com.hapnium.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HapQrCodeException extends RuntimeException {
  private final String code;

  public HapQrCodeException(String code, String message) {
    super(message);
    this.code = code;
  }

  public HapQrCodeException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}