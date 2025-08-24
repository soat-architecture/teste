package dev.com.soat.autorepairshop.infrastructure.exception;

import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.api.models.response.DefaultResponseDTO;
import dev.com.soat.autorepairshop.infrastructure.exception.models.ExceptionDTO;
import dev.com.soat.autorepairshop.infrastructure.exception.models.ValidationErrorDTO;
import dev.com.soat.autorepairshop.infrastructure.exception.models.ValidationFieldErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final String GENERIC_ERROR = "Generic Error";
    private final MessageSource messageSource;

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DefaultResponseDTO<ExceptionDTO> handleRepositoryException(
            DomainException ex,
            HttpServletRequest request
    ) {
        final var message = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );
        final var data = new ExceptionDTO(
                "Business Error",
                message,
                request.getRequestURI()
        );
        return new DefaultResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                data,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public DefaultResponseDTO<ExceptionDTO> handleConflictException(
            ConflictException ex,
            HttpServletRequest request
    ) {
        final var message = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );
        final var data = new ExceptionDTO(
                "Conflict Error",
                message,
                request.getRequestURI()
        );
        return new DefaultResponseDTO<>(
                HttpStatus.CONFLICT.value(),
                data,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public DefaultResponseDTO<ExceptionDTO> handleNotFoundException(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        final var message = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );
        final var data = new ExceptionDTO(
                "Not Found Error",
                message,
                request.getRequestURI()
        );
        return new DefaultResponseDTO<>(
                HttpStatus.NOT_FOUND.value(),
                data,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @ExceptionHandler(GenericException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DefaultResponseDTO<ExceptionDTO> handleGenericException(
            GenericException ex,
            HttpServletRequest request
    ) {
        final var message = messageSource.getMessage(
                ex.getMessage(),
                ex.getArgs(),
                LocaleContextHolder.getLocale()
        );
        final var data = new ExceptionDTO(
                GENERIC_ERROR,
                message,
                request.getRequestURI()
        );
        return new DefaultResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DefaultResponseDTO<ExceptionDTO> handleAllOtherExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        final var message = messageSource.getMessage(
                "generic.error",
                null,
                LocaleContextHolder.getLocale()
        );
        final var data = new ExceptionDTO(
                GENERIC_ERROR,
                message,
                request.getRequestURI()
        );
        return new DefaultResponseDTO<>(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                data,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DefaultResponseDTO<ValidationErrorDTO> handleValidationWithDetails(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        final List<ValidationFieldErrorDTO> fieldErrors = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> fieldErrors.add(
                new ValidationFieldErrorDTO(
                    error.getField(),
                    error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
                    error.getDefaultMessage()
                )
        ));

        ValidationErrorDTO errorResponse = new ValidationErrorDTO(
                "Validation failed",
                fieldErrors,
                request.getRequestURI()
        );

        return new DefaultResponseDTO<>(
                HttpStatus.BAD_REQUEST.value(),
                errorResponse,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
