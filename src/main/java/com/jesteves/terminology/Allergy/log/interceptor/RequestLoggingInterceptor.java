package com.jesteves.terminology.Allergy.log.interceptor;

import com.jesteves.terminology.Allergy.log.entity.RequestLog;
import com.jesteves.terminology.Allergy.log.service.RequestLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String START_TIME_ATTR = "startTime";

    private final RequestLogService requestLogService;

    @Autowired
    public RequestLoggingInterceptor(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Execute BEFORE the controller
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);
        log.trace("Request Begin: {} {}", request.getMethod(), request.getRequestURI());
        return true; //continues the process
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Execute AFTER the request was completed (even in case of error)
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long duration = (startTime != null) ? System.currentTimeMillis() - startTime : -1; // Duration

        RequestLog logEntry = new RequestLog();
        logEntry.setTimestamp(LocalDateTime.now());
        logEntry.setMethod(request.getMethod());

        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUri = requestUri;
        if (queryString != null && !queryString.isEmpty()) {
            fullUri += "?" + queryString;
        }
        logEntry.setUri(fullUri); //Full URI

        logEntry.setRemoteAddr(getClientIpAddress(request)); // Client IP
        logEntry.setUserAgent(request.getHeader("User-Agent")); //  User-Agent
        logEntry.setStatusCode(response.getStatus()); // response status
        logEntry.setDurationMs(duration);

        // save the log
        requestLogService.saveLog(logEntry);

        log.trace("End Request: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), duration);


        if (ex != null) {
            log.error("Exception not handled during request {} {}: {}",
                    request.getMethod(), request.getRequestURI(), ex.getMessage());
        }
    }

    // helper method to obtain the IP real (avoid proxies)
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        return ipAddress;
    }
}