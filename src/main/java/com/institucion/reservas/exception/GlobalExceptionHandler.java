package com.institucion.reservas.exception;

import com.institucion.reservas.dto.error.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Clock clock;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        return build(exception.getStatus(), exception.getCode(), exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> details = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        exception.getBindingResult().getGlobalErrors().forEach(error ->
                details.putIfAbsent(error.getObjectName(), error.getDefaultMessage())
        );
        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDACION",
                "La solicitud contiene datos inválidos",
                request,
                details
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> details = new LinkedHashMap<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            details.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDACION",
                "La solicitud contiene datos inválidos",
                request,
                details
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "JSON_INVALIDO",
                "El cuerpo de la solicitud no tiene un formato JSON válido o contiene valores no permitidos",
                request,
                null
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String detail = "El parámetro '%s' tiene un valor no válido".formatted(exception.getName());
        return build(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO", detail, request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        String detail = "El parámetro '%s' es obligatorio".formatted(exception.getParameterName());
        return build(HttpStatus.BAD_REQUEST, "PARAMETRO_INVALIDO", detail, request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METODO_NO_PERMITIDO",
                "El método HTTP no está permitido para esta ruta",
                request,
                null
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "TIPO_CONTENIDO_NO_SOPORTADO",
                "El tipo de contenido de la solicitud no está soportado",
                request,
                null
        );
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_ACCEPTABLE,
                "TIPO_RESPUESTA_NO_ACEPTABLE",
                "No se puede generar una respuesta con el tipo de contenido solicitado",
                request,
                null
        );
    }

    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponse> handleRouteNotFound(
            Exception exception,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.NOT_FOUND,
                "RUTA_NO_ENCONTRADA",
                "La ruta solicitada no existe",
                request,
                null
        );
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handlePessimisticLock(
            PessimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        log.warn("Conflicto de concurrencia en {}: {}", request.getRequestURI(), exception.getMessage());
        return build(
                HttpStatus.CONFLICT,
                "CONFLICTO_CONCURRENCIA",
                "No se pudo completar la operación por concurrencia; intente nuevamente",
                request,
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        String cause = exception.getMostSpecificCause().getMessage();
        String normalized = cause == null ? "" : cause.toLowerCase(Locale.ROOT);
        log.warn("Violación de integridad de datos en {}: {}", request.getRequestURI(), cause);

        if (normalized.contains("uk_usuario_documento")
                || normalized.contains("uk_usuario_correo")
                || normalized.contains("unique")) {
            return build(
                    HttpStatus.CONFLICT,
                    "DATOS_DUPLICADOS",
                    "Ya existe un registro con los mismos datos únicos",
                    request,
                    null
            );
        }

        return build(
                HttpStatus.BAD_REQUEST,
                "DATOS_INVALIDOS",
                "Los datos no cumplen las restricciones de integridad",
                request,
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Error no controlado en {}", request.getRequestURI(), exception);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERROR_INTERNO",
                "Ocurrió un error inesperado. Intente nuevamente o contacte al administrador",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            Map<String, String> details
    ) {
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(clock),
                status.value(),
                code,
                message,
                request.getRequestURI(),
                details
        );
        return ResponseEntity.status(status).body(response);
    }
}
