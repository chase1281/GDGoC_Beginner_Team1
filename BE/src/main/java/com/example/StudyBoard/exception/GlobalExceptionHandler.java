package com.example.StudyBoard.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponseBody> businessExceptionHandler(BusinessException e){
        String message = e.getMessage();

        if(message != null){
            return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ExceptionResponseBody.of(e.getErrorCode(), message));
        }
        else{
            return ResponseEntity
                    .status(e.getErrorCode().getHttpStatus())
                    .body(ExceptionResponseBody.of(e.getErrorCode()));
        }
    }

    //@Valid 유효성 검사
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseBody> validationExceptionHandler(MethodArgumentNotValidException e){
        List<String> errors = new ArrayList<>();

        for(FieldError fieldError: e.getBindingResult().getFieldErrors()){
            errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getHttpStatus())
                .body(ExceptionResponseBody.of(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    //권한 처리
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ExceptionResponseBody> authorizationDeniedExceptionHandler(
            AuthorizationDeniedException e
    ){
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ExceptionResponseBody.of(ErrorCode.FORBIDDEN_ACCESS));
    }

    //나머지 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseBody> exceptionHandler(Exception e){
        e.printStackTrace();
        return ResponseEntity
                .status(ErrorCode.SERVER_EXCEPTION.getHttpStatus())
                .body(ExceptionResponseBody.of(ErrorCode.SERVER_EXCEPTION, e.getMessage()));
    }
}
