package surfExtractor.exceptions;

public class UnsuportedImageException extends Exception {

	public UnsuportedImageException() {
		super();
	}

	public UnsuportedImageException(String message) {
		super(message);
	}

	public UnsuportedImageException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsuportedImageException(Throwable cause) {
		super(cause);
	}
}
