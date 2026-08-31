package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "abc_xyz_analysis", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "analysis_date"}))
public class ABCXYZAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "abc_category", nullable = false, length = 1)
    private String abcCategory;

    @Column(name = "xyz_category", nullable = false, length = 1)
    private String xyzCategory;

    @Column(name = "sales_contribution", nullable = false, precision = 5, scale = 2)
    private BigDecimal salesContribution;

    @Column(name = "demand_variability", nullable = false, precision = 5, scale = 2)
    private BigDecimal demandVariability;

    @Column(name = "recommended_strategy", length = 100)
    private String recommendedStrategy;

    @Column(name = "analysis_date", nullable = false)
    private LocalDate analysisDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public ABCXYZAnalysis() {}

    public ABCXYZAnalysis(Book book, String abcCategory, String xyzCategory, 
                         BigDecimal salesContribution, BigDecimal demandVariability, LocalDate analysisDate) {
        this.book = book;
        this.abcCategory = abcCategory;
        this.xyzCategory = xyzCategory;
        this.salesContribution = salesContribution;
        this.demandVariability = demandVariability;
        this.analysisDate = analysisDate;
        this.recommendedStrategy = generateRecommendedStrategy(abcCategory, xyzCategory);
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (recommendedStrategy == null) {
            recommendedStrategy = generateRecommendedStrategy(abcCategory, xyzCategory);
        }
    }

    private String generateRecommendedStrategy(String abc, String xyz) {
        String combination = abc + xyz;
        switch (combination) {
            case "AX": return "重点管理 - 高頻度発注";
            case "AY": return "需要予測強化 - 中頻度発注";
            case "AZ": return "機会損失回避 - 安全在庫確保";
            case "BX": return "効率管理 - 定期発注";
            case "BY": return "標準管理 - 月次発注";
            case "BZ": return "柔軟対応 - 四半期発注";
            case "CX": return "最小管理 - 低頻度発注";
            case "CY": return "見直し対象 - オンデマンド";
            case "CZ": return "廃止検討 - 在庫処分";
            default: return "要分析";
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getAbcCategory() { return abcCategory; }
    public void setAbcCategory(String abcCategory) { this.abcCategory = abcCategory; }

    public String getXyzCategory() { return xyzCategory; }
    public void setXyzCategory(String xyzCategory) { this.xyzCategory = xyzCategory; }

    public BigDecimal getSalesContribution() { return salesContribution; }
    public void setSalesContribution(BigDecimal salesContribution) { this.salesContribution = salesContribution; }

    public BigDecimal getDemandVariability() { return demandVariability; }
    public void setDemandVariability(BigDecimal demandVariability) { this.demandVariability = demandVariability; }

    public String getRecommendedStrategy() { return recommendedStrategy; }
    public void setRecommendedStrategy(String recommendedStrategy) { this.recommendedStrategy = recommendedStrategy; }

    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}