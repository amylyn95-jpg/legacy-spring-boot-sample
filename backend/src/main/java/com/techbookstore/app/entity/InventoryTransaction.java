package com.techbookstore.app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Inventory transaction history for audit trail and tracking
 */
@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType type;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "before_quantity", nullable = false)
    private Integer beforeQuantity;

    @Column(name = "after_quantity", nullable = false)
    private Integer afterQuantity;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "batch_number", length = 50)
    private String batchNumber;

    @Column(name = "reference_number", length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by")
    private User executedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Constructors
    public InventoryTransaction() {
        this.createdAt = LocalDateTime.now();
        this.executedAt = LocalDateTime.now();
    }

    public InventoryTransaction(Inventory inventory, TransactionType type, Integer quantity,
                               Integer beforeQuantity, Integer afterQuantity, User executedBy) {
        this();
        this.inventory = inventory;
        this.type = type;
        this.quantity = quantity;
        this.beforeQuantity = beforeQuantity;
        this.afterQuantity = afterQuantity;
        this.executedBy = executedBy;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getBeforeQuantity() { return beforeQuantity; }
    public void setBeforeQuantity(Integer beforeQuantity) { this.beforeQuantity = beforeQuantity; }

    public Integer getAfterQuantity() { return afterQuantity; }
    public void setAfterQuantity(Integer afterQuantity) { this.afterQuantity = afterQuantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public User getExecutedBy() { return executedBy; }
    public void setExecutedBy(User executedBy) { this.executedBy = executedBy; }

    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}