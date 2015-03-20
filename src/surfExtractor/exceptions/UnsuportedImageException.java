package surfExtractor.exceptions;

public class UnsuportedImageException extends Exception {

	/**
	 * Eclipse/Java thingy
	 */
	private static final long serialVersionUID = 382321553948544134L;

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
