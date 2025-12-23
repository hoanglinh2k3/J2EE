package com.example.fbclone.exception;

public class ForbiddenException extends ApiException {
  public ForbiddenException(String message) {
    super(message);
  }
}
