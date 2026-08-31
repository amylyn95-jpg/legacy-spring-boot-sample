package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity for optimal stock settings
 * 最適在庫設定エンティティ
 */
@Entity
@Table(name = "optimal_stock_settings")
public class OptimalStockSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "optimal_stock_level", nullable = false)
    private Integer optimalStockLevel;

    @Column(name = "reorder_point", nullable = false)
    private Integer reorderPoint;

    @Column(name = "safety_stock", nullable = false)
    private Integer safetyStock;

    @Column(name = "economic_order_quantity")
    private Integer economicOrderQuantity;

    @Column(name = "obsolescence_factor", precision = 4, scale = 3)
    private BigDecimal obsolescenceFactor;

    @Column(name = "trend_factor", precision = 4, scale = 3)
    private BigDecimal trendFactor;

    @Column(name = "seasonality_factor", precision = 4, scale = 3)
    private BigDecimal seasonalityFactor;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    // Constructors
    public OptimalStockSettings() {}

    public OptimalStockSettings(Book book, Integer optimalStockLevel, Integer reorderPoint, 
                               Integer safetyStock, Integer economicOrderQuantity) {
        this.book = book;
        this.optimalStockLevel = optimalStockLevel;
        this.reorderPoint = reorderPoint;
        this.safetyStock = safetyStock;
        this.economicOrderQuantity = economicOrderQuantity;
        this.validFrom = LocalDate.now();
        this.obsolescenceFactor = BigDecimal.ONE;
        this.trendFactor = BigDecimal.ONE;
        this.seasonalityFactor = BigDecimal.ONE;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getOptimalStockLevel() { return optimalStockLevel; }
    public void setOptimalStockLevel(Integer optimalStockLevel) { this.optimalStockLevel = optimalStockLevel; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }

    public Integer getEconomicOrderQuantity() { return economicOrderQuantity; }
    public void setEconomicOrderQuantity(Integer economicOrderQuantity) { this.economicOrderQuantity = economicOrderQuantity; }

    public BigDecimal getObsolescenceFactor() { return obsolescenceFactor; }
    public void setObsolescenceFactor(BigDecimal obsolescenceFactor) { this.obsolescenceFactor = obsolescenceFactor; }

    public BigDecimal getTrendFactor() { return trendFactor; }
    public void setTrendFactor(BigDecimal trendFactor) { this.trendFactor = trendFactor; }

    public BigDecimal getSeasonalityFactor() { return seasonalityFactor; }
    public void setSeasonalityFactor(BigDecimal seasonalityFactor) { this.seasonalityFactor = seasonalityFactor; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
}