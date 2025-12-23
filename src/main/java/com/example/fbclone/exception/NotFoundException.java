package com.example.fbclone.exception;

public class NotFoundException extends ApiException {
  public NotFoundException(String message) {
    super(message);
  }
}
