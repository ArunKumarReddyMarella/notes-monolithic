package com.enotes.monolithic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LoggingUtil {
    
    public Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
    
    public void logUserActivity(String username, String action, String details) {
        Logger userActivityLogger = LoggerFactory.getLogger("USER_ACTIVITY");
        userActivityLogger.info("User: {} - Action: {} - Details: {}", username, action, details);
    }
    
    public void logSecurityEvent(String username, String event, String details) {
        Logger securityLogger = LoggerFactory.getLogger("SECURITY");
        securityLogger.info("User: {} - Event: {} - Details: {}", username, event, details);
    }
}
