package net.media.adscert.exceptions;

public class ProcessException extends VerificationServiceException {
	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessException(String message) {
		super(message);
	}

	public ProcessException(Throwable cause) {
		super(cause);
	}
}
