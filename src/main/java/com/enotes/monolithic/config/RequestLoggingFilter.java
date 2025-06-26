package com.enotes.monolithic.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestLoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);
        
        String requestId = UUID.randomUUID().toString();
        
        long startTime = System.currentTimeMillis();
        logger.info("REQUEST [{}] {} {} started", requestId, httpRequest.getMethod(), httpRequest.getRequestURI());
        
        try {
            chain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            logger.info("REQUEST [{}] {} {} completed in {} ms with status {}", 
                    requestId, 
                    httpRequest.getMethod(), 
                    httpRequest.getRequestURI(), 
                    duration, 
                    responseWrapper.getStatus());
            
            responseWrapper.copyBodyToResponse();
        }
    }
}
