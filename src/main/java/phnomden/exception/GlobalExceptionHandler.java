package phnomden.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorCode ErrorCode = new ErrorCode("RESOURCE_NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(ErrorCode, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex) {
        ErrorCode ErrorCode = new ErrorCode("INTERNAL_SERVER_ERROR", "An unexpected error occurred.");
        return new ResponseEntity<>(ErrorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}