package com.veterinaria.consentidos.core.middleware;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ErrorHandler Tests")
class ErrorHandlerTest {
    
    private ErrorHandler errorHandler;
    
    @Mock
    private MethodArgumentNotValidException validationException;
    
    @Mock
    private BindingResult bindingResult;
    
    @Mock
    private WebRequest webRequest;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        errorHandler = new ErrorHandler();
    }
    
    @Test
    @DisplayName("Should handle validation exceptions correctly")
    void testHandleValidationExceptions() {
        // Arrange
        FieldError fieldError1 = new FieldError("object", "field1", "error message 1");
        FieldError fieldError2 = new FieldError("object", "field2", "error message 2");
        List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);
        
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
        
        // Act
        ResponseEntity<Map<String, Object>> response = errorHandler.handleValidationExceptions(validationException, webRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Validation Failed", body.get("error"));
        assertEquals("Invalid input parameters", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) body.get("errors");
        assertEquals("error message 1", errors.get("field1"));
        assertEquals("error message 2", errors.get("field2"));
    }
    
    @Test
    @DisplayName("Should handle illegal argument exceptions correctly")
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument provided");
        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
        
        // Act
        ResponseEntity<Map<String, Object>> response = errorHandler.handleIllegalArgumentException(exception, webRequest);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(400, body.get("status"));
        assertEquals("Invalid Argument", body.get("error"));
        assertEquals("Invalid argument provided", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
    
    @Test
    @DisplayName("Should handle runtime exceptions correctly")
    void testHandleRuntimeException() {
        // Arrange
        RuntimeException exception = new RuntimeException("Something went wrong");
        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
        
        // Act
        ResponseEntity<Map<String, Object>> response = errorHandler.handleRuntimeException(exception, webRequest);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    @DisplayName("Should handle security exceptions correctly")
    void testHandleSecurityException() {
        // Arrange
        SecurityException exception = new SecurityException("Invalid credentials");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/auth/login");

        // Act
        ResponseEntity<Map<String, Object>> response = errorHandler.handleSecurityException(exception, webRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(401, body.get("status"));
        assertEquals("Unauthorized", body.get("error"));
        assertEquals("Invalid credentials", body.get("message"));
        assertEquals("/api/auth/login", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
    
    @Test
    @DisplayName("Should handle generic exceptions correctly")
    void testHandleGenericException() {
        // Arrange
        Exception exception = new Exception("Generic error");
        when(webRequest.getDescription(false)).thenReturn("uri=/test/path");
        
        // Act
        ResponseEntity<Map<String, Object>> response = errorHandler.handleGenericException(exception, webRequest);
        
        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals(500, body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertEquals("An unexpected error occurred", body.get("message"));
        assertEquals("/test/path", body.get("path"));
        assertNotNull(body.get("timestamp"));
    }
}