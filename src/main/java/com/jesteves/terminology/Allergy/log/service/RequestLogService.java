package com.jesteves.terminology.Allergy.log.service;

import com.jesteves.terminology.Allergy.log.repository.RequestLogRepository;
import com.jesteves.terminology.Allergy.log.entity.RequestLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RequestLogService {

    private static final Logger log = LoggerFactory.getLogger(RequestLogService.class);
    private final RequestLogRepository requestLogRepository;

    @Autowired
    public RequestLogService(RequestLogRepository requestLogRepository) {
        this.requestLogRepository = requestLogRepository;
    }

    @Transactional(value = "pgTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void saveLog(RequestLog logEntry) {
        try {
            requestLogRepository.save(logEntry);
        } catch (Exception e) {
            log.error("Fail saving log in PostgreSQL: {}", e.getMessage());
        }
    }
}