package dev.com.soat.autorepairshop.infrastructure.exception;

import dev.com.soat.autorepairshop.domain.exception.template.ConflictException;
import dev.com.soat.autorepairshop.domain.exception.template.DomainException;
import dev.com.soat.autorepairshop.domain.exception.template.GenericException;
import dev.com.soat.autorepairshop.domain.exception.template.NotFoundException;
import dev.com.soat.autorepairshop.infrastructure.exception.models.ExceptionDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private MessageSource messageSource;
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        handler = new GlobalExceptionHandler(messageSource);
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test-path");
        LocaleContextHolder.setLocale(Locale.ENGLISH);
    }

    @Test
    void shouldHandleDomainException() {
        var ex = new DomainException("domain.error");
        when(messageSource.getMessage(eq("domain.error"), any(), any()))
                .thenReturn("Domain error occurred");

        var response = handler.handleRepositoryException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
        assertTrue(response.data() instanceof ExceptionDTO dto && dto.error().equals("Business Error"));
    }

    @Test
    void shouldHandleConflictException() {
        var ex = new ConflictException("conflict.error");
        when(messageSource.getMessage(eq("conflict.error"), any(), any()))
                .thenReturn("Conflict error occurred");

        var response = handler.handleConflictException(ex, request);

        assertEquals(HttpStatus.CONFLICT.value(), response.status());
        assertTrue(response.data() instanceof ExceptionDTO dto && dto.error().equals("Conflict Error"));
    }

    @Test
    void shouldHandleNotFoundException() {
        var ex = new NotFoundException("notfound.error");
        when(messageSource.getMessage(eq("notfound.error"), any(), any()))
                .thenReturn("Not found");

        var response = handler.handleNotFoundException(ex, request);

        assertEquals(HttpStatus.NOT_FOUND.value(), response.status());
        assertTrue(response.data() instanceof ExceptionDTO dto && dto.error().equals("Not Found Error"));
    }

    @Test
    void shouldHandleGenericException() {
        var ex = new GenericException("generic.error");
        when(messageSource.getMessage(eq("generic.error"), any(), any()))
                .thenReturn("Internal error");

        var response = handler.handleGenericException(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertTrue(response.data() instanceof ExceptionDTO dto && dto.error().equals("Generic Error"));
    }

    @Test
    void shouldHandleUnknownException() {
        var ex = new RuntimeException("unexpected");
        when(messageSource.getMessage(eq("generic.error"), any(), any()))
                .thenReturn("Something went wrong");

        var response = handler.handleAllOtherExceptions(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.status());
        assertTrue(response.data() instanceof ExceptionDTO dto && dto.error().equals("Generic Error"));
    }

    @Test
    void shouldHandleValidationErrors() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("object", "email", "invalid@email", false, null, null, "Invalid email");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidationWithDetails(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.status());
    }
}
