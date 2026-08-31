package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demand_forecasts")
public class DemandForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "forecast_date", nullable = false)
    private LocalDate forecastDate;

    @Column(name = "predicted_demand", nullable = false)
    private Integer predictedDemand;

    @Column(name = "algorithm", nullable = false, length = 50)
    private String algorithm = "SEASONAL";

    @Column(name = "confidence", nullable = false)
    private Double confidence = 0.7500;

    @Column(name = "parameters", columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public DemandForecast() {}

    public DemandForecast(Book book, LocalDate forecastDate, Integer predictedDemand, String algorithm, Double confidence) {
        this.book = book;
        this.forecastDate = forecastDate;
        this.predictedDemand = predictedDemand;
        this.algorithm = algorithm;
        this.confidence = confidence;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }

    public Integer getPredictedDemand() { return predictedDemand; }
    public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }

    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}