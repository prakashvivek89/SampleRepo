package exceptions;

public class MissingFileException extends Exception {

	public MissingFileException(String errorMessage) {
		super(errorMessage);
	}
}