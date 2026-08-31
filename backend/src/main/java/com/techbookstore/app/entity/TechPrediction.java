package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for technology prediction data
 * 技術予測データエンティティ
 */
@Entity
@Table(name = "tech_predictions")
public class TechPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tech_category_id", nullable = false)
    private TechCategory techCategory;

    @Column(name = "prediction_date", nullable = false)
    private LocalDate predictionDate;

    @Column(name = "prediction_for_date", nullable = false)
    private LocalDate predictionForDate;

    @Column(name = "predicted_revenue", precision = 12, scale = 2)
    private BigDecimal predictedRevenue;

    @Column(name = "predicted_growth_rate", precision = 5, scale = 2)
    private BigDecimal predictedGrowthRate;

    @Column(name = "confidence_interval", precision = 5, scale = 2)
    private BigDecimal confidenceInterval;

    @Column(name = "prediction_model", length = 50)
    private String predictionModel;

    @Column(name = "model_accuracy", precision = 5, scale = 2)
    private BigDecimal modelAccuracy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public TechPrediction() {
        this.createdAt = LocalDateTime.now();
    }

    public TechPrediction(TechCategory techCategory, LocalDate predictionDate, LocalDate predictionForDate) {
        this();
        this.techCategory = techCategory;
        this.predictionDate = predictionDate;
        this.predictionForDate = predictionForDate;
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

    public LocalDate getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(LocalDate predictionDate) {
        this.predictionDate = predictionDate;
    }

    public LocalDate getPredictionForDate() {
        return predictionForDate;
    }

    public void setPredictionForDate(LocalDate predictionForDate) {
        this.predictionForDate = predictionForDate;
    }

    public BigDecimal getPredictedRevenue() {
        return predictedRevenue;
    }

    public void setPredictedRevenue(BigDecimal predictedRevenue) {
        this.predictedRevenue = predictedRevenue;
    }

    public BigDecimal getPredictedGrowthRate() {
        return predictedGrowthRate;
    }

    public void setPredictedGrowthRate(BigDecimal predictedGrowthRate) {
        this.predictedGrowthRate = predictedGrowthRate;
    }

    public BigDecimal getConfidenceInterval() {
        return confidenceInterval;
    }

    public void setConfidenceInterval(BigDecimal confidenceInterval) {
        this.confidenceInterval = confidenceInterval;
    }

    public String getPredictionModel() {
        return predictionModel;
    }

    public void setPredictionModel(String predictionModel) {
        this.predictionModel = predictionModel;
    }

    public BigDecimal getModelAccuracy() {
        return modelAccuracy;
    }

    public void setModelAccuracy(BigDecimal modelAccuracy) {
        this.modelAccuracy = modelAccuracy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}