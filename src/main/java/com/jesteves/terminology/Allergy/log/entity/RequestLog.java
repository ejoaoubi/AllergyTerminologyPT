package com.jesteves.terminology.Allergy.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_logs")
@Getter
@Setter
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(length = 10)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String uri;

    @Column(name = "remote_addr", length = 50)
    private String remoteAddr;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "duration_ms")
    private Long durationMs;

    public RequestLog() {
        this.timestamp = LocalDateTime.now();
    }




}