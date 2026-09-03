package com.techbookstore.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregation_cache")
public class AggregationCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cache_key", unique = true, nullable = false, length = 255)
    @NotBlank(message = "Cache key is required")
    @Size(max = 255, message = "Cache key must not exceed 255 characters")
    private String cacheKey;
    
    @Column(name = "aggregation_type", nullable = false, length = 50)
    @NotBlank(message = "Aggregation type is required")
    @Size(max = 50, message = "Aggregation type must not exceed 50 characters")
    private String aggregationType;
    
    @Column(name = "aggregation_date", nullable = false)
    @NotNull(message = "Aggregation date is required")
    private LocalDate aggregationDate;
    
    @Column(name = "aggregation_data", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Aggregation data is required")
    private String aggregationData;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    @NotNull(message = "Expires at is required")
    private LocalDateTime expiresAt;
    
    // Constructors
    public AggregationCache() {
        this.createdAt = LocalDateTime.now();
    }
    
    public AggregationCache(String cacheKey, String aggregationType, LocalDate aggregationDate, 
                           String aggregationData, LocalDateTime expiresAt) {
        this();
        this.cacheKey = cacheKey;
        this.aggregationType = aggregationType;
        this.aggregationDate = aggregationDate;
        this.aggregationData = aggregationData;
        this.expiresAt = expiresAt;
    }
    
    // Convenience constructor for simple caching
    public AggregationCache(String cacheKey, String aggregationData, LocalDateTime expiresAt) {
        this();
        this.cacheKey = cacheKey;
        this.aggregationType = "general";
        this.aggregationDate = LocalDate.now();
        this.aggregationData = aggregationData;
        this.expiresAt = expiresAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCacheKey() {
        return cacheKey;
    }
    
    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }
    
    public String getAggregationType() {
        return aggregationType;
    }
    
    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }
    
    public LocalDate getAggregationDate() {
        return aggregationDate;
    }
    
    public void setAggregationDate(LocalDate aggregationDate) {
        this.aggregationDate = aggregationDate;
    }
    
    public String getAggregationData() {
        return aggregationData;
    }
    
    public void setAggregationData(String aggregationData) {
        this.aggregationData = aggregationData;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    // Utility methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
    
    // Alias methods for backward compatibility
    public String getKeyName() {
        return this.cacheKey;
    }
    
    public void setKeyName(String keyName) {
        this.cacheKey = keyName;
    }
    
    public String getValueData() {
        return this.aggregationData;
    }
    
    public void setValueData(String valueData) {
        this.aggregationData = valueData;
    }
}
