package com.hapnium.core.exception;

import com.hapnium.core.qr_code.enums.QrCodeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HapQrCodeException extends RuntimeException {
  private final QrCodeStatus code;

  public HapQrCodeException(QrCodeStatus code, String message) {
    super(message);
    this.code = code;
  }

  public HapQrCodeException(QrCodeStatus code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }
}