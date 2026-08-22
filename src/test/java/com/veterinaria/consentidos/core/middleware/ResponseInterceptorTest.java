package com.veterinaria.consentidos.core.middleware;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ResponseInterceptor Tests")
class ResponseInterceptorTest {
    
    private ResponseInterceptor responseInterceptor;
    
    @Mock
    private MethodParameter returnType;
    
    @Mock
    private ServerHttpRequest request;
    
    @Mock
    private ServerHttpResponse response;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        responseInterceptor = new ResponseInterceptor();
    }
    
    @Test
    @DisplayName("Should support all return types")
    void testSupports() {
        assertTrue(responseInterceptor.supports(returnType, null));
    }
    
    @Test
    @DisplayName("Should not wrap response that already contains timestamp")
    void testShouldNotWrapResponseWithTimestamp() {
        // Arrange
        Map<String, Object> existingResponse = new HashMap<>();
        existingResponse.put("timestamp", "2023-01-01T00:00:00");
        existingResponse.put("data", "test");
        
        when(request.getURI()).thenReturn(URI.create("/api/test"));
        
        // Act
        Object result = responseInterceptor.beforeBodyWrite(
            existingResponse, returnType, MediaType.APPLICATION_JSON, 
            null, request, response
        );
        
        // Assert
        assertEquals(existingResponse, result);
    }
    
    @Test
    @DisplayName("Should not wrap string responses")
    void testShouldNotWrapStringResponses() {
        // Arrange
        String stringResponse = "Hello World";
        when(request.getURI()).thenReturn(URI.create("/api/test"));
        
        // Act
        Object result = responseInterceptor.beforeBodyWrite(
            stringResponse, returnType, MediaType.TEXT_PLAIN, 
            null, request, response
        );
        
        // Assert
        assertEquals(stringResponse, result);
    }
    
    @Test
    @DisplayName("Should wrap simple data object in standardized response")
    void testShouldWrapSimpleDataObject() {
        // Arrange
        Map<String, String> data = new HashMap<>();
        data.put("name", "Firulais");
        data.put("species", "Dog");
        
        when(request.getURI()).thenReturn(URI.create("/api/pets/1"));
        
        // Act
        Object result = responseInterceptor.beforeBodyWrite(
            data, returnType, MediaType.APPLICATION_JSON, 
            null, request, response
        );
        
        // Assert
        assertEquals(data, result);
    }
    
    @Test
    @DisplayName("Should create error response correctly")
    void testCreateErrorResponse() {
        // Act
        Map<String, Object> errorResponse = ResponseInterceptor.createErrorResponse(
            404, "Pet not found", "/api/pets/999"
        );
        
        // Assert
        assertEquals(404, errorResponse.get("status"));
        assertEquals(false, errorResponse.get("success"));
        assertEquals("Pet not found", errorResponse.get("message"));
        assertNull(errorResponse.get("data"));
        assertEquals("/api/pets/999", errorResponse.get("path"));
        assertNotNull(errorResponse.get("timestamp"));
    }
}