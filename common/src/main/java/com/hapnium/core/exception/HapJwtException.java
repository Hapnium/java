package com.hapnium.core.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HapJwtException extends RuntimeException {
  private final String code;

  public HapJwtException(String code, String message) {
    super(message);
    this.code = code;
  }

  public HapJwtException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}