package com.techbookstore.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "report_configs")
public class ReportConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 20)
    @NotNull(message = "Report type is required")
    private ReportType reportType;
    
    @Column(name = "report_name", nullable = false, length = 100)
    @NotBlank(message = "Report name is required")
    @Size(max = 100, message = "Report name must not exceed 100 characters")
    private String reportName;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Column(name = "config_json", columnDefinition = "TEXT")
    private String configJson;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Enums
    public enum ReportType {
        SALES, INVENTORY, CUSTOMER, TREND, DASHBOARD, CUSTOM
    }
    
    // Constructors
    public ReportConfig() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public ReportConfig(ReportType reportType, String reportName, String description) {
        this();
        this.reportType = reportType;
        this.reportName = reportName;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ReportType getReportType() {
        return reportType;
    }
    
    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }
    
    public String getReportName() {
        return reportName;
    }
    
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getConfigJson() {
        return configJson;
    }
    
    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}