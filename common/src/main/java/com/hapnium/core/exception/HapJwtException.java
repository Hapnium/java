package com.hapnium.core.exception;

import com.hapnium.core.jwt.enums.JwtStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HapJwtException extends RuntimeException {
  private final JwtStatus code;

  public HapJwtException(JwtStatus code, String message) {
    super(message);
    this.code = code;
  }

  public HapJwtException(JwtStatus code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}