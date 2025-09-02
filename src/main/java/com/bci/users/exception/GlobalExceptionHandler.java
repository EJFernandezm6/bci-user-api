package com.bci.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String,String>> onBusiness(BusinessException ex){
        return ResponseEntity.status(ex.getStatus()).body(Map.of("mensaje", ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> onValidation(MethodArgumentNotValidException ex){
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField()+": "+err.getDefaultMessage())
                .findFirst().orElse("Solicitud inválida");
        return ResponseEntity.badRequest().body(Map.of("mensaje", msg));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> onGeneric(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error no controlado"));
    }
}
