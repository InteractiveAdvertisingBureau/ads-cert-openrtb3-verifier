package net.media.adscert.exceptions;

public class InvalidDataException extends VerificationServiceException {

  public InvalidDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidDataException(String message) {
    super(message);
  }

  public InvalidDataException(Throwable cause) {
    super(cause);
  }
}
