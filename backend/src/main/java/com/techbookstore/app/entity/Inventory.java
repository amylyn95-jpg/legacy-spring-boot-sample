package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "book_id", nullable = false, unique = true)
    private Book book;

    @Column(name = "store_stock")
    private Integer storeStock = 0;

    @Column(name = "warehouse_stock")
    private Integer warehouseStock = 0;

    @Column(name = "reserved_count")
    private Integer reservedCount = 0;

    @Column(name = "location_code", length = 20)
    private String locationCode;

    @Column(name = "reorder_point")
    private Integer reorderPoint;

    @Column(name = "reorder_quantity")
    private Integer reorderQuantity;

    @Column(name = "last_received_date")
    private LocalDate lastReceivedDate;

    @Column(name = "last_sold_date")
    private LocalDate lastSoldDate;

    // Additional fields for analytics (Phase 1 enhancement)
    @Column(name = "minimum_stock_level")
    private Integer minimumStockLevel = 0;

    @Column(name = "maximum_stock_level")
    private Integer maximumStockLevel;

    @Column(name = "safety_stock_level")
    private Integer safetyStockLevel = 0;

    @Column(name = "abc_classification", length = 1)
    private String abcClassification;

    @Column(name = "xyz_classification", length = 1)
    private String xyzClassification;

    @Column(name = "last_stocktaking_date")
    private LocalDate lastStocktakingDate;

    @Column(name = "average_lead_time_days")
    private Integer averageLeadTimeDays = 7;

    @Column(name = "turnover_rate")
    private Double turnoverRate;

    @Column(name = "days_since_last_sale")
    private Integer daysSinceLastSale;

    // Constructors
    public Inventory() {}

    public Inventory(Book book) {
        this.book = book;
    }

    // Calculated methods
    public Integer getTotalStock() {
        return (storeStock != null ? storeStock : 0) + (warehouseStock != null ? warehouseStock : 0);
    }

    public Integer getAvailableStock() {
        return getTotalStock() - (reservedCount != null ? reservedCount : 0);
    }

    public boolean isLowStock() {
        return reorderPoint != null && getTotalStock() <= reorderPoint;
    }

    public boolean isOutOfStock() {
        return getTotalStock() <= 0;
    }

    public boolean isOverStock() {
        return maximumStockLevel != null && getTotalStock() > maximumStockLevel;
    }

    public String getStockStatus() {
        if (isOutOfStock()) return "OUT_OF_STOCK";
        if (isLowStock()) return "LOW_STOCK";
        if (isOverStock()) return "OVER_STOCK";
        return "NORMAL";
    }

    public boolean isDeadStock() {
        return daysSinceLastSale != null && daysSinceLastSale > 90;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public Integer getStoreStock() { return storeStock; }
    public void setStoreStock(Integer storeStock) { this.storeStock = storeStock; }

    public Integer getWarehouseStock() { return warehouseStock; }
    public void setWarehouseStock(Integer warehouseStock) { this.warehouseStock = warehouseStock; }

    public Integer getReservedCount() { return reservedCount; }
    public void setReservedCount(Integer reservedCount) { this.reservedCount = reservedCount; }

    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public LocalDate getLastReceivedDate() { return lastReceivedDate; }
    public void setLastReceivedDate(LocalDate lastReceivedDate) { this.lastReceivedDate = lastReceivedDate; }

    public LocalDate getLastSoldDate() { return lastSoldDate; }
    public void setLastSoldDate(LocalDate lastSoldDate) { this.lastSoldDate = lastSoldDate; }

    public Integer getMinimumStockLevel() { return minimumStockLevel; }
    public void setMinimumStockLevel(Integer minimumStockLevel) { this.minimumStockLevel = minimumStockLevel; }

    public Integer getMaximumStockLevel() { return maximumStockLevel; }
    public void setMaximumStockLevel(Integer maximumStockLevel) { this.maximumStockLevel = maximumStockLevel; }

    public Integer getSafetyStockLevel() { return safetyStockLevel; }
    public void setSafetyStockLevel(Integer safetyStockLevel) { this.safetyStockLevel = safetyStockLevel; }

    public String getAbcClassification() { return abcClassification; }
    public void setAbcClassification(String abcClassification) { this.abcClassification = abcClassification; }

    public String getXyzClassification() { return xyzClassification; }
    public void setXyzClassification(String xyzClassification) { this.xyzClassification = xyzClassification; }

    public LocalDate getLastStocktakingDate() { return lastStocktakingDate; }
    public void setLastStocktakingDate(LocalDate lastStocktakingDate) { this.lastStocktakingDate = lastStocktakingDate; }

    public Integer getAverageLeadTimeDays() { return averageLeadTimeDays; }
    public void setAverageLeadTimeDays(Integer averageLeadTimeDays) { this.averageLeadTimeDays = averageLeadTimeDays; }

    public Double getTurnoverRate() { return turnoverRate; }
    public void setTurnoverRate(Double turnoverRate) { this.turnoverRate = turnoverRate; }

    public Integer getDaysSinceLastSale() { return daysSinceLastSale; }
    public void setDaysSinceLastSale(Integer daysSinceLastSale) { this.daysSinceLastSale = daysSinceLastSale; }
}