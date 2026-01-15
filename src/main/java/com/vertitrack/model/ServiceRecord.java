package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lift_id", nullable = false)
    private Lift lift;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType serviceType;
    
    @Column(nullable = false)
    private LocalDate serviceDate;
    
    private LocalDate nextServiceDate;
    
    @Column(nullable = false)
    private String performedBy; // Technician/Company name
    
    @Column(length = 2000)
    private String workDescription;
    
    @Column(length = 1000)
    private String partsReplaced;
    
    @Column(length = 1000)
    private String issuesFound;
    
    private Double laborCost;
    
    private Double partsCost;
    
    private Double totalCost;
    
    @Enumerated(EnumType.STRING)
    private ServiceStatus status = ServiceStatus.COMPLETED;
    
    @Column(length = 500)
    private String remarks;
    
    private String invoiceNumber;
    
    private LocalDate invoiceDate;
    
    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum ServiceType {
        AMC_SERVICING,      // Regular AMC service
        AMC_REPAIR,         // Repair under AMC
        BREAKDOWN_REPAIR,   // Emergency repair
        PREVENTIVE_MAINTENANCE,
        QUARTERLY_SERVICE,
        ANNUAL_INSPECTION,
        PARTS_REPLACEMENT,
        OTHER
    }
    
    public enum ServiceStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}
