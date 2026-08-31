package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for technology trend analysis data
 * 技術トレンド分析データエンティティ
 */
@Entity
@Table(name = "tech_trend_analysis")
public class TechTrendAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tech_category_id", nullable = false)
    private TechCategory techCategory;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "total_revenue", precision = 12, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "total_units_sold")
    private Integer totalUnitsSold;

    @Column(name = "growth_rate", precision = 5, scale = 2)
    private BigDecimal growthRate;

    @Column(name = "market_share", precision = 5, scale = 2)
    private BigDecimal marketShare;

    @Enumerated(EnumType.STRING)
    @Column(name = "lifecycle_stage", length = 20)
    private LifecycleStage lifecycleStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "trend_direction", length = 20)
    private TrendDirection trendDirection;

    @Column(name = "emerging_score", precision = 5, scale = 2)
    private BigDecimal emergingScore;

    @Column(name = "obsolescence_risk", precision = 5, scale = 2)
    private BigDecimal obsolescenceRisk;

    @Column(name = "trend_analysis", columnDefinition = "TEXT")
    private String trendAnalysis;

    @Column(name = "investment_recommendation", columnDefinition = "TEXT")
    private String investmentRecommendation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enums
    public enum LifecycleStage {
        EMERGING, GROWTH, MATURITY, DECLINE
    }

    public enum TrendDirection {
        RISING, STABLE, DECLINING
    }

    // Constructors
    public TechTrendAnalysis() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public TechTrendAnalysis(TechCategory techCategory, LocalDate analysisDate) {
        this();
        this.techCategory = techCategory;
        this.analysisDate = analysisDate;
    }

    // JPA lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TechCategory getTechCategory() {
        return techCategory;
    }

    public void setTechCategory(TechCategory techCategory) {
        this.techCategory = techCategory;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Integer getTotalUnitsSold() {
        return totalUnitsSold;
    }

    public void setTotalUnitsSold(Integer totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }

    public BigDecimal getGrowthRate() {
        return growthRate;
    }

    public void setGrowthRate(BigDecimal growthRate) {
        this.growthRate = growthRate;
    }

    public BigDecimal getMarketShare() {
        return marketShare;
    }

    public void setMarketShare(BigDecimal marketShare) {
        this.marketShare = marketShare;
    }

    public LifecycleStage getLifecycleStage() {
        return lifecycleStage;
    }

    public void setLifecycleStage(LifecycleStage lifecycleStage) {
        this.lifecycleStage = lifecycleStage;
    }

    public TrendDirection getTrendDirection() {
        return trendDirection;
    }

    public void setTrendDirection(TrendDirection trendDirection) {
        this.trendDirection = trendDirection;
    }

    public BigDecimal getEmergingScore() {
        return emergingScore;
    }

    public void setEmergingScore(BigDecimal emergingScore) {
        this.emergingScore = emergingScore;
    }

    public BigDecimal getObsolescenceRisk() {
        return obsolescenceRisk;
    }

    public void setObsolescenceRisk(BigDecimal obsolescenceRisk) {
        this.obsolescenceRisk = obsolescenceRisk;
    }

    public String getTrendAnalysis() {
        return trendAnalysis;
    }

    public void setTrendAnalysis(String trendAnalysis) {
        this.trendAnalysis = trendAnalysis;
    }

    public String getInvestmentRecommendation() {
        return investmentRecommendation;
    }

    public void setInvestmentRecommendation(String investmentRecommendation) {
        this.investmentRecommendation = investmentRecommendation;
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
}