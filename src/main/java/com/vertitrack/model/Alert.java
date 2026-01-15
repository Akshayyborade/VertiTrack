package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertPriority priority;
    
    @Column(nullable = false, length = 500)
    private String title;
    
    @Column(length = 2000)
    private String message;
    
    @Column(nullable = false)
    private LocalDate alertDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lift_id")
    private Lift lift;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;
    
    @Column(nullable = false)
    private Boolean isRead = false;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    private LocalDate dueDate;
    
    private LocalDateTime readAt;
    
    private LocalDateTime dismissedAt;
    
    @Column(length = 500)
    private String actionTaken;
    
    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum AlertType {
        AMC_EXPIRY,              // AMC expiring soon
        AMC_RENEWAL,             // AMC renewal due
        QUARTERLY_PAYMENT,       // Quarterly payment due
        SERVICE_DUE,             // Regular service due
        REPAIR_REQUIRED,         // Repair needed
        PAYMENT_OVERDUE,         // Payment overdue
        EMPLOYEE_ABSENCE,        // Employee absent
        OTHER
    }
    
    public enum AlertPriority {
        CRITICAL,  // Red - Immediate attention (0-7 days)
        HIGH,      // Orange - Urgent (8-15 days)
        MEDIUM,    // Yellow - Important (16-30 days)
        LOW        // Blue - Info (30+ days)
    }
}
