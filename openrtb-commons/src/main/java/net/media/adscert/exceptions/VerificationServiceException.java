package net.media.adscert.exceptions;

public class VerificationServiceException extends RuntimeException {

  public VerificationServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public VerificationServiceException(String message) {
    super(message);
  }

  public VerificationServiceException(Throwable cause) {
    super(cause);
  }
}
