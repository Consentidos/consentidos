package com.veterinaria.consentidos.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("RequestInterceptor Tests")
class RequestInterceptorTest {
    
    private RequestInterceptor requestInterceptor;
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    @Mock
    private Object handler;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestInterceptor = new RequestInterceptor();
    }
    
    @Test
    @DisplayName("Should handle request with existing correlation ID")
    void testPreHandleWithExistingCorrelationId() throws Exception {
        // Arrange
        String existingCorrelationId = "existing-correlation-id";
        when(request.getHeader("X-Correlation-ID")).thenReturn(existingCorrelationId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        
        // Act
        boolean result = requestInterceptor.preHandle(request, response, handler);
        
        // Assert
        assertTrue(result);
        verify(response).setHeader("X-Correlation-ID", existingCorrelationId);
        verify(request).setAttribute(eq("startTime"), any(Long.class));
    }
    
    @Test
    @DisplayName("Should generate correlation ID when not present")
    void testPreHandleWithoutCorrelationId() throws Exception {
        // Arrange
        when(request.getHeader("X-Correlation-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        
        // Act
        boolean result = requestInterceptor.preHandle(request, response, handler);
        
        // Assert
        assertTrue(result);
        verify(response).setHeader(eq("X-Correlation-ID"), any(String.class));
        verify(request).setAttribute(eq("startTime"), any(Long.class));
    }
    
    @Test
    @DisplayName("Should generate correlation ID when empty")
    void testPreHandleWithEmptyCorrelationId() throws Exception {
        // Arrange
        when(request.getHeader("X-Correlation-ID")).thenReturn("   ");
        when(request.getMethod()).thenReturn("PUT");
        when(request.getRequestURI()).thenReturn("/api/update");
        
        // Act
        boolean result = requestInterceptor.preHandle(request, response, handler);
        
        // Assert
        assertTrue(result);
        verify(response).setHeader(eq("X-Correlation-ID"), any(String.class));
        verify(request).setAttribute(eq("startTime"), any(Long.class));
    }
    
    @Test
    @DisplayName("Should complete request successfully")
    void testAfterCompletionSuccess() throws Exception {
        // Arrange
        long startTime = System.currentTimeMillis() - 100;
        String correlationId = "test-correlation-id";
        
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(response.getHeader("X-Correlation-ID")).thenReturn(correlationId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/test");
        when(response.getStatus()).thenReturn(200);
        
        // Act
        requestInterceptor.afterCompletion(request, response, handler, null);
        
        // Assert
        // No exception should be thrown, and logging should occur
        verify(request).getAttribute("startTime");
        verify(response).getHeader("X-Correlation-ID");
    }
    
    @Test
    @DisplayName("Should complete request with error")
    void testAfterCompletionWithError() throws Exception {
        // Arrange
        long startTime = System.currentTimeMillis() - 200;
        String correlationId = "test-correlation-id";
        Exception exception = new RuntimeException("Test error");
        
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(response.getHeader("X-Correlation-ID")).thenReturn(correlationId);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/error");
        when(response.getStatus()).thenReturn(500);
        
        // Act
        requestInterceptor.afterCompletion(request, response, handler, exception);
        
        // Assert
        // No exception should be thrown, and error logging should occur
        verify(request).getAttribute("startTime");
        verify(response).getHeader("X-Correlation-ID");
    }
    
    @Test
    @DisplayName("Should extract client IP from X-Forwarded-For header")
    void testGetClientIpAddressFromXForwardedFor() {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");
        
        // Act
        String clientIp = RequestInterceptor.getClientIpAddress(request);
        
        // Assert
        assertEquals("192.168.1.1", clientIp);
    }
    
    @Test
    @DisplayName("Should extract client IP from X-Real-IP header")
    void testGetClientIpAddressFromXRealIp() {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.100");
        
        // Act
        String clientIp = RequestInterceptor.getClientIpAddress(request);
        
        // Assert
        assertEquals("192.168.1.100", clientIp);
    }
    
    @Test
    @DisplayName("Should extract client IP from remote address")
    void testGetClientIpAddressFromRemoteAddr() {
        // Arrange
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        String clientIp = RequestInterceptor.getClientIpAddress(request);
        
        // Assert
        assertEquals("127.0.0.1", clientIp);
    }
    
    @Test
    @DisplayName("Should identify health check requests")
    void testIsHealthCheckRequest() {
        // Health check endpoints
        when(request.getRequestURI()).thenReturn("/actuator/health");
        assertTrue(RequestInterceptor.isHealthCheckRequest(request));
        
        when(request.getRequestURI()).thenReturn("/health");
        assertTrue(RequestInterceptor.isHealthCheckRequest(request));
        
        when(request.getRequestURI()).thenReturn("/ping");
        assertTrue(RequestInterceptor.isHealthCheckRequest(request));
        
        when(request.getRequestURI()).thenReturn("/api/v1/actuator/health/db");
        assertTrue(RequestInterceptor.isHealthCheckRequest(request));
        
        // Non-health check endpoints
        when(request.getRequestURI()).thenReturn("/api/users");
        assertFalse(RequestInterceptor.isHealthCheckRequest(request));
        
        when(request.getRequestURI()).thenReturn("/api/v1/products");
        assertFalse(RequestInterceptor.isHealthCheckRequest(request));
    }
}