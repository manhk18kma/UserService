package RestFulApi.TTCS.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class , ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception exception , WebRequest webRequest){
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri=" , ""));
        errorResponse.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        String  message = exception.getMessage();
        System.out.println(message);
        if (exception instanceof MethodArgumentNotValidException){
            int start =  message.lastIndexOf("[");
            int end = message.lastIndexOf("]]");
            message = message.substring(start+1 ,end );
            errorResponse.setError("Payload invalid");
        } else if (exception instanceof ConstraintViolationException){
            int start =  message.lastIndexOf(".");
            int end = message.length();
            message = message.substring(start+1 ,end );
            errorResponse.setError("Parameter invalid");

        }
        errorResponse.setMessage(message);
        return errorResponse;

    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalException(Exception exception , WebRequest webRequest){
        System.out.println("Handle handleInternalException");
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setPath(webRequest.getDescription(false).replace("uri=" , ""));
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        if(exception instanceof  MethodArgumentTypeMismatchException){
            errorResponse.setMessage("Failed to convert value of type");
        }
        return errorResponse;

    }
}
