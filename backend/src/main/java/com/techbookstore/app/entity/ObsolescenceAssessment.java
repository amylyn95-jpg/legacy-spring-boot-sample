package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "obsolescence_assessments")
public class ObsolescenceAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "risk_level", nullable = false, length = 10)
    private String riskLevel;

    @Column(name = "months_to_obsolescence")
    private Integer monthsToObsolescence;

    @Column(name = "risk_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(name = "mitigation_strategy", length = 200)
    private String mitigationStrategy;

    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public ObsolescenceAssessment() {}

    public ObsolescenceAssessment(Book book, String riskLevel, Integer monthsToObsolescence, 
                                BigDecimal riskScore, LocalDate assessmentDate) {
        this.book = book;
        this.riskLevel = riskLevel;
        this.monthsToObsolescence = monthsToObsolescence;
        this.riskScore = riskScore;
        this.assessmentDate = assessmentDate;
        this.mitigationStrategy = generateMitigationStrategy(riskLevel, monthsToObsolescence);
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (mitigationStrategy == null) {
            mitigationStrategy = generateMitigationStrategy(riskLevel, monthsToObsolescence);
        }
    }

    private String generateMitigationStrategy(String riskLevel, Integer monthsToObsolescence) {
        if ("HIGH".equals(riskLevel)) {
            if (monthsToObsolescence != null && monthsToObsolescence <= 6) {
                return "即座に割引販売・サプライヤー返品検討";
            } else {
                return "在庫削減・新版入荷停止検討";
            }
        } else if ("MEDIUM".equals(riskLevel)) {
            return "販売促進・マーケティング強化";
        } else {
            return "継続監視・定期評価";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Integer getMonthsToObsolescence() { return monthsToObsolescence; }
    public void setMonthsToObsolescence(Integer monthsToObsolescence) { this.monthsToObsolescence = monthsToObsolescence; }

    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }

    public String getMitigationStrategy() { return mitigationStrategy; }
    public void setMitigationStrategy(String mitigationStrategy) { this.mitigationStrategy = mitigationStrategy; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}