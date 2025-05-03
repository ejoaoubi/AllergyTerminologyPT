package com.jesteves.terminology.Allergy.log.repository;

import com.jesteves.terminology.Allergy.log.entity.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {

}