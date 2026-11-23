package com.juntavecinos.auth.exception;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> solicitudInvalida(BadRequestException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> validacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getDefaultMessage()).distinct().collect(Collectors.joining(". "));
        return respuesta(HttpStatus.BAD_REQUEST, mensaje, request);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class })
    public ResponseEntity<ApiErrorResponse> formato(Exception ex, HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, "Los datos enviados no tienen un formato válido", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> sinPermiso(AccessDeniedException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.FORBIDDEN, "Su perfil no tiene permiso para esta acción", request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> noEncontrado(ResourceNotFoundException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> rutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, "Recurso no encontrado", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> metodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> tipoContenido(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no permitido", request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> conflicto(ConflictException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler({ DataIntegrityViolationException.class, ConcurrencyFailureException.class })
    public ResponseEntity<ApiErrorResponse> conflictoDatos(Exception ex, HttpServletRequest request) {
        logger.warn("Conflicto de datos en {}: {}", request.getRequestURI(), ex.getMessage());
        return respuesta(HttpStatus.CONFLICT, "El registro fue modificado o ya existe. Actualice e intente nuevamente", request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> noAutorizado(UnauthorizedException ex, HttpServletRequest request) {
        return respuesta(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> general(Exception ex, HttpServletRequest request) {
        logger.error("Excepcion no controlada en {}", request.getRequestURI(), ex);
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno", request);
    }

    private ResponseEntity<ApiErrorResponse> respuesta(HttpStatus status, String mensaje, HttpServletRequest request) {
        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(mensaje)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
