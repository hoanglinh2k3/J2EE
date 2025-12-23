package com.example.fbclone.exception;

public class ApiException extends RuntimeException {
  public ApiException(String message) {
    super(message);
  }
}
