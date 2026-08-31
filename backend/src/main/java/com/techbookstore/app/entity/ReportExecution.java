package com.techbookstore.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_executions")
public class ReportExecution {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "report_config_id")
    private Long reportConfigId;
    
    @Column(name = "executed_by", length = 100)
    @Size(max = 100, message = "Executed by must not exceed 100 characters")
    private String executedBy;
    
    @Column(name = "executed_at", nullable = false)
    @NotNull(message = "Executed at is required")
    private LocalDateTime executedAt;
    
    @Column(columnDefinition = "TEXT")
    private String parameters;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Status is required")
    private ExecutionStatus status;
    
    @Column(name = "result_path", length = 500)
    @Size(max = 500, message = "Result path must not exceed 500 characters")
    private String resultPath;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "execution_time_ms")
    private Long executionTimeMs;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Enums
    public enum ExecutionStatus {
        RUNNING, COMPLETED, FAILED, CANCELLED
    }
    
    // Constructors
    public ReportExecution() {
        this.createdAt = LocalDateTime.now();
        this.executedAt = LocalDateTime.now();
        this.status = ExecutionStatus.RUNNING;
    }
    
    public ReportExecution(Long reportConfigId, String executedBy) {
        this();
        this.reportConfigId = reportConfigId;
        this.executedBy = executedBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getReportConfigId() {
        return reportConfigId;
    }
    
    public void setReportConfigId(Long reportConfigId) {
        this.reportConfigId = reportConfigId;
    }
    
    public String getExecutedBy() {
        return executedBy;
    }
    
    public void setExecutedBy(String executedBy) {
        this.executedBy = executedBy;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    public ExecutionStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
    
    public String getResultPath() {
        return resultPath;
    }
    
    public void setResultPath(String resultPath) {
        this.resultPath = resultPath;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}