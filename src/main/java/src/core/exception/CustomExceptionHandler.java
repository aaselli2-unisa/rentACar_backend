package src.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import src.controller.TResponse;
import src.core.exception.response.ErrorResponse;
import src.core.exception.type.NotFoundExceptionType;
import src.core.exception.type.ValidationExceptionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static src.core.exception.ErrorLogConstant.*;

@RestControllerAdvice
public class CustomExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    // V-06: expose field-level validation details only in non-production environments
    @Value("${app.expose-validation-details:false}")
    private boolean exposeValidationDetails;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public TResponse<?> handleException(Exception e) {
        // Security patch V08: raw e.getMessage() replaced with a generic string.
        // The full exception (with stack trace and DB details) is kept in the server log only.
        log(ERROR_GENERIC_EXCEPTION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(NotFoundExceptionType.GENERIC_EXCEPTION,
                        Collections.singletonList("An internal error occurred. Please contact support.")))
                .build();
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public TResponse<?> handleDataNotFoundException(DataNotFoundException e) {
        log(ERROR_DATA_NOT_FOUND, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getNotFoundExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(FileException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public TResponse<?> handleFileException(FileException e) {
        log(ERROR_FILE, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getFileExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleAlreadyExistsException(AlreadyExistsException e) {
        log(ERROR_ALREADY_EXISTS, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getAlreadyExistsExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(PaymentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handlePaymentException(PaymentException e) {
        log(ERROR_PAYMENT, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getPaymentExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(NotSuitableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleNotSuitableException(NotSuitableException e) {
        log(ERROR_NOT_SUITABLE, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getNotSuitableExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleValidationException(ValidationException e) {
        log(ERROR_VALIDATION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(e.getValidationExceptionType(), Collections.singletonList(e.getDetail())))
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleMissingRequestParameter(MissingServletRequestParameterException e) {
        log(ERROR_VALIDATION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(ValidationExceptionType.VALIDATION_EXCEPTION,
                        Collections.singletonList("Missing required parameter: " + e.getParameterName())))
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log(ERROR_VALIDATION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(ValidationExceptionType.VALIDATION_EXCEPTION,
                        Collections.singletonList("Invalid parameter type: " + e.getName())))
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleInvalidBody(HttpMessageNotReadableException e) {
        log(ERROR_VALIDATION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(ValidationExceptionType.VALIDATION_EXCEPTION,
                        Collections.singletonList("Malformed request body")))
                .build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public TResponse<?> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException e) {
        log(ERROR_VALIDATION, e);
        return TResponse.tResponseBuilder()
                .response(new ErrorResponse(ValidationExceptionType.VALIDATION_EXCEPTION,
                        Collections.singletonList("Unsupported media type")))
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public TResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> validationErrors = new ArrayList<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.add(fieldName + ": " + errorMessage);
        });

        ValidationExceptionType validationExceptionType = ValidationExceptionType.VALIDATION_EXCEPTION;
        ValidationException validationException = new ValidationException(validationExceptionType, "Validation error");
        validationException.setDetail(validationErrors.toString());

        log(ERROR_VALIDATION, validationException);
        ErrorResponse errorResponse = new ErrorResponse(validationExceptionType, Collections.singletonList("Validation error"));

        // V-06: field names leaked to clients allow enumeration of DTO structure;
        // expose details only in dev (app.expose-validation-details=true)
        errorResponse.setDetails(exposeValidationDetails
                ? validationErrors
                : Collections.singletonList("Validation error"));

        return TResponse.tResponseBuilder()
                .response(errorResponse)
                .build();
    }

    private void log(String errorLogConstant, Exception e) {
        logger.error(errorLogConstant, e);
    }
}
