package com.veterinaria.consentidos.core.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interceptor for logging requests and adding correlation IDs
 */
@Component
public class RequestInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestInterceptor.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // Generate or extract correlation ID
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }
        
        // Add correlation ID to response header
        response.setHeader(CORRELATION_ID_HEADER, correlationId);
        
        // Store start time for performance measurement
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Log request
        logger.info("Incoming request: {} {} - Correlation ID: {} - Timestamp: {}", 
                   request.getMethod(), 
                   request.getRequestURI(), 
                   correlationId,
                   LocalDateTime.now());
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                              Object handler, Exception ex) throws Exception {
        
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        String correlationId = response.getHeader(CORRELATION_ID_HEADER);
        
        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (ex != null) {
                logger.error("Request completed with error: {} {} - Status: {} - Time: {}ms - Correlation ID: {} - Error: {}", 
                           request.getMethod(), 
                           request.getRequestURI(),
                           response.getStatus(),
                           executionTime,
                           correlationId,
                           ex.getMessage());
            } else {
                logger.info("Request completed: {} {} - Status: {} - Time: {}ms - Correlation ID: {}", 
                          request.getMethod(), 
                          request.getRequestURI(),
                          response.getStatus(),
                          executionTime,
                          correlationId);
            }
        }
    }
    
    /**
     * Extracts client IP address from request
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Checks if request is from a health check endpoint
     */
    public static boolean isHealthCheckRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.contains("/actuator/health") || 
               uri.contains("/health") || 
               uri.contains("/ping");
    }
}
