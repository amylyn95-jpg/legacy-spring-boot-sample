package com.techbookstore.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for custom report requests.
 * Enhanced for Phase 4 custom report functionality.
 */
public class CustomReportRequest {
    
    @NotBlank(message = "Report type is required")
    @Size(max = 50, message = "Report type must not exceed 50 characters")
    private String reportType;
    
    @Size(max = 100, message = "Report name must not exceed 100 characters")
    private String reportName;
    
    private LocalDate startDate;
    private LocalDate endDate;
    
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
    
    @Size(max = 500, message = "Parameters must not exceed 500 characters")
    private String parameters;
    
    private String createdBy;
    private String description;
    private Map<String, Object> filters;
    private String templateId;
    
    // Constructors
    public CustomReportRequest() {}
    
    public CustomReportRequest(String reportType, LocalDate startDate, LocalDate endDate) {
        this.reportType = reportType;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public CustomReportRequest(String reportType, String reportName, String createdBy) {
        this.reportType = reportType;
        this.reportName = reportName;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public String getReportType() { 
        return reportType; 
    }
    
    public void setReportType(String reportType) { 
        this.reportType = reportType; 
    }
    
    public String getReportName() {
        return reportName;
    }
    
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
    
    public LocalDate getStartDate() { 
        return startDate; 
    }
    
    public void setStartDate(LocalDate startDate) { 
        this.startDate = startDate; 
    }
    
    public LocalDate getEndDate() { 
        return endDate; 
    }
    
    public void setEndDate(LocalDate endDate) { 
        this.endDate = endDate; 
    }
    
    public String getCategory() { 
        return category; 
    }
    
    public void setCategory(String category) { 
        this.category = category; 
    }
    
    public String getParameters() { 
        return parameters; 
    }
    
    public void setParameters(String parameters) { 
        this.parameters = parameters; 
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<String, Object> getFilters() {
        return filters;
    }
    
    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    // Validation methods
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Optional dates
        }
        return !startDate.isAfter(endDate);
    }
}