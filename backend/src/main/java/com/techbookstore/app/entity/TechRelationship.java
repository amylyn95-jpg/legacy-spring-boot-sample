package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for technology relationship analysis
 * 技術関連性分析エンティティ
 */
@Entity
@Table(name = "tech_relationships")
public class TechRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "primary_tech_id", nullable = false)
    private TechCategory primaryTech;

    @ManyToOne
    @JoinColumn(name = "related_tech_id", nullable = false)
    private TechCategory relatedTech;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", length = 20, nullable = false)
    private RelationshipType relationshipType;

    @Column(name = "correlation_strength", precision = 5, scale = 2, nullable = false)
    private BigDecimal correlationStrength;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level", length = 10)
    private ConfidenceLevel confidenceLevel;

    @Column(name = "statistical_significance", precision = 5, scale = 4)
    private BigDecimal statisticalSignificance;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Enums
    public enum RelationshipType {
        COMPLEMENTARY, COMPETITIVE, PREREQUISITE, SUCCESSOR
    }

    public enum ConfidenceLevel {
        HIGH, MEDIUM, LOW
    }

    // Constructors
    public TechRelationship() {
        this.createdAt = LocalDateTime.now();
    }

    public TechRelationship(TechCategory primaryTech, TechCategory relatedTech, 
                           RelationshipType relationshipType, BigDecimal correlationStrength) {
        this();
        this.primaryTech = primaryTech;
        this.relatedTech = relatedTech;
        this.relationshipType = relationshipType;
        this.correlationStrength = correlationStrength;
        this.analysisDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TechCategory getPrimaryTech() {
        return primaryTech;
    }

    public void setPrimaryTech(TechCategory primaryTech) {
        this.primaryTech = primaryTech;
    }

    public TechCategory getRelatedTech() {
        return relatedTech;
    }

    public void setRelatedTech(TechCategory relatedTech) {
        this.relatedTech = relatedTech;
    }

    public RelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(RelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public BigDecimal getCorrelationStrength() {
        return correlationStrength;
    }

    public void setCorrelationStrength(BigDecimal correlationStrength) {
        this.correlationStrength = correlationStrength;
    }

    public LocalDate getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDate analysisDate) {
        this.analysisDate = analysisDate;
    }

    public ConfidenceLevel getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(ConfidenceLevel confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public BigDecimal getStatisticalSignificance() {
        return statisticalSignificance;
    }

    public void setStatisticalSignificance(BigDecimal statisticalSignificance) {
        this.statisticalSignificance = statisticalSignificance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}