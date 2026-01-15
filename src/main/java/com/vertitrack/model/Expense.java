package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType expenseType;
    
    @Column(nullable = false)
    private Double amount;
    
    @Column(nullable = false)
    private LocalDate expenseDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lift_id")
    private Lift lift;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_record_id")
    private ServiceRecord serviceRecord;
    
    @Column(nullable = false)
    private String category; // AMC Payment, Repair Cost, Material, Petrol, etc.
    
    @Column(length = 1000)
    private String description;
    
    private String paidTo;
    
    private String paymentMode; // Cash, Cheque, Online, UPI, etc.
    
    private String transactionReference;
    
    private String invoiceNumber;
    
    private LocalDate invoiceDate;
    
    @Column(length = 500)
    private String remarks;
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PAID;
    
    // For recurring expenses
    private Boolean isRecurring = false;
    
    private String recurringFrequency; // Monthly, Quarterly, Yearly
    
    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum ExpenseType {
        AMC_PAYMENT,           // Annual AMC payment
        QUARTERLY_PAYMENT,     // Quarterly AMC payment
        REPAIR_PAYMENT,        // Repair costs
        MATERIAL_EXPENSE,      // Materials for AMC/Repair
        EMPLOYEE_PETROL,       // Employee petrol expense
        EMPLOYEE_OTHER,        // Other employee expenses
        PARTS_PURCHASE,        // Spare parts
        LABOR_CHARGES,         // Labor costs
        OTHER
    }
    
    public enum PaymentStatus {
        PAID, PENDING, OVERDUE, CANCELLED
    }
}
