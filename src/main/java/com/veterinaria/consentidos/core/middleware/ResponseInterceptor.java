package com.veterinaria.consentidos.core.middleware;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor for standardizing API responses in the veterinary application
 */
@ControllerAdvice
public class ResponseInterceptor implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(MethodParameter returnType, 
                          Class<? extends HttpMessageConverter<?>> converterType) {
        // Simple implementation - apply to all controllers
        return true;
    }
    
    @Override
    public Object beforeBodyWrite(Object body, 
                                MethodParameter returnType,
                                MediaType selectedContentType, 
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, 
                                ServerHttpResponse response) {
        
        // Don't wrap if already a standardized response
        if (body instanceof Map && ((Map<?, ?>) body).containsKey("timestamp")) {
            return body;
        }
        
        // Don't wrap String responses (like health checks)
        if (body instanceof String) {
            return body;
        }
        
        // Create standardized success response
        return createSuccessResponse(body, request);
    }
    
    /**
     * Creates a standardized success response
     */
    private Map<String, Object> createSuccessResponse(Object data, ServerHttpRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("success", true);
        response.put("message", "Request processed successfully");
        response.put("data", data);
        response.put("path", request.getURI().getPath());
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    /**
     * Creates a standardized error response (static method for use in error handlers)
     */
    public static Map<String, Object> createErrorResponse(int status, String message, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status);
        response.put("success", false);
        response.put("message", message);
        response.put("data", null);
        response.put("path", path);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}