package co.psyke.test_coverage.exception;

public class InsufficientCreditsException extends RuntimeException {
    
    public InsufficientCreditsException(String message) {
        super(message);
    }
    
    public InsufficientCreditsException(String message, Throwable cause) {
        super(message, cause);
    }
}
