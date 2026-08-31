package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for tracking forecast accuracy metrics
 * 予測精度記録エンティティ
 */
@Entity
@Table(name = "forecast_accuracy")
public class ForecastAccuracy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "algorithm", nullable = false, length = 50)
    private String algorithm;

    @Column(name = "mae", precision = 8, scale = 2)
    private BigDecimal mae; // Mean Absolute Error

    @Column(name = "mape", precision = 5, scale = 2)
    private BigDecimal mape; // Mean Absolute Percentage Error

    @Column(name = "rmse", precision = 8, scale = 2)
    private BigDecimal rmse; // Root Mean Square Error

    @Column(name = "evaluation_period_start")
    private LocalDate evaluationPeriodStart;

    @Column(name = "evaluation_period_end")
    private LocalDate evaluationPeriodEnd;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public ForecastAccuracy() {}

    public ForecastAccuracy(String algorithm, BigDecimal mae, BigDecimal mape, BigDecimal rmse, 
                           LocalDate evaluationPeriodStart, LocalDate evaluationPeriodEnd) {
        this.algorithm = algorithm;
        this.mae = mae;
        this.mape = mape;
        this.rmse = rmse;
        this.evaluationPeriodStart = evaluationPeriodStart;
        this.evaluationPeriodEnd = evaluationPeriodEnd;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public BigDecimal getMae() { return mae; }
    public void setMae(BigDecimal mae) { this.mae = mae; }

    public BigDecimal getMape() { return mape; }
    public void setMape(BigDecimal mape) { this.mape = mape; }

    public BigDecimal getRmse() { return rmse; }
    public void setRmse(BigDecimal rmse) { this.rmse = rmse; }

    public LocalDate getEvaluationPeriodStart() { return evaluationPeriodStart; }
    public void setEvaluationPeriodStart(LocalDate evaluationPeriodStart) { this.evaluationPeriodStart = evaluationPeriodStart; }

    public LocalDate getEvaluationPeriodEnd() { return evaluationPeriodEnd; }
    public void setEvaluationPeriodEnd(LocalDate evaluationPeriodEnd) { this.evaluationPeriodEnd = evaluationPeriodEnd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}