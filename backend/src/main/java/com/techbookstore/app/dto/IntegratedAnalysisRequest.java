package com.techbookstore.app.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for integrated inventory analysis
 * Supports comprehensive analysis across all phases
 */
public class IntegratedAnalysisRequest {
    
    // Common analysis parameters
    private String category;
    private String level;
    private String publisher;
    private String stockStatus;
    private String priceRange;
    private String publicationYear;
    
    // Phase-specific parameters
    private Integer forecastHorizon = 30; // days
    private Boolean includeOptimization = true;
    private Boolean includeRealTimeData = false;
    private List<String> analysisTypes; // ["abc_xyz", "forecasting", "optimization"]
    
    // Performance parameters
    private Boolean asyncExecution = false;
    private Integer maxExecutionTimeSeconds = 60;
    private String cacheStrategy = "default"; // "none", "aggressive", "default"
    
    // Analysis date
    private LocalDate analysisDate;
    
    // Constructors
    public IntegratedAnalysisRequest() {
        this.analysisDate = LocalDate.now();
    }
    
    // Cache key generation for Redis caching
    public String cacheKey() {
        StringBuilder key = new StringBuilder("integrated_analysis");
        if (category != null) key.append("_cat:").append(category);
        if (level != null) key.append("_lvl:").append(level);
        if (publisher != null) key.append("_pub:").append(publisher);
        if (stockStatus != null) key.append("_stock:").append(stockStatus);
        if (priceRange != null) key.append("_price:").append(priceRange);
        if (publicationYear != null) key.append("_year:").append(publicationYear);
        if (analysisTypes != null) key.append("_types:").append(String.join(",", analysisTypes));
        key.append("_horizon:").append(forecastHorizon);
        key.append("_opt:").append(includeOptimization);
        key.append("_date:").append(analysisDate.toString());
        return key.toString();
    }
    
    // Getters and setters
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
    
    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }
    
    public String getPublicationYear() { return publicationYear; }
    public void setPublicationYear(String publicationYear) { this.publicationYear = publicationYear; }
    
    public Integer getForecastHorizon() { return forecastHorizon; }
    public void setForecastHorizon(Integer forecastHorizon) { this.forecastHorizon = forecastHorizon; }
    
    public Boolean getIncludeOptimization() { return includeOptimization; }
    public void setIncludeOptimization(Boolean includeOptimization) { this.includeOptimization = includeOptimization; }
    
    public Boolean getIncludeRealTimeData() { return includeRealTimeData; }
    public void setIncludeRealTimeData(Boolean includeRealTimeData) { this.includeRealTimeData = includeRealTimeData; }
    
    public List<String> getAnalysisTypes() { return analysisTypes; }
    public void setAnalysisTypes(List<String> analysisTypes) { this.analysisTypes = analysisTypes; }
    
    public Boolean getAsyncExecution() { return asyncExecution; }
    public void setAsyncExecution(Boolean asyncExecution) { this.asyncExecution = asyncExecution; }
    
    public Integer getMaxExecutionTimeSeconds() { return maxExecutionTimeSeconds; }
    public void setMaxExecutionTimeSeconds(Integer maxExecutionTimeSeconds) { this.maxExecutionTimeSeconds = maxExecutionTimeSeconds; }
    
    public String getCacheStrategy() { return cacheStrategy; }
    public void setCacheStrategy(String cacheStrategy) { this.cacheStrategy = cacheStrategy; }
    
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
}