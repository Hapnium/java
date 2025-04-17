package com.hapnium.core.exception;

public class HapStorageException extends RuntimeException {
  public HapStorageException(String message) {
    super(message);
  }

  public HapStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}