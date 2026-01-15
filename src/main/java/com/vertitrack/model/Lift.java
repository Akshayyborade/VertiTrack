package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lifts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lift {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String liftNumber;
    
    @Column(nullable = false)
    private String location;
    
    private String building;
    
    private String liftType; // Passenger, Freight, Service, etc.
    
    private Integer capacity; // in kg or persons
    
    private Integer floors;
    
    @Column(length = 500)
    private String manufacturer;
    
    private String model;
    
    private LocalDate installationDate;
    
    // AMC Details
    @Column(nullable = false)
    private LocalDate amcStartDate;
    
    @Column(nullable = false)
    private LocalDate amcEndDate;
    
    @Column(nullable = false)
    private LocalDate amcRenewalDate;
    
    private Double amcAmount;
    
    // Quarterly Payment Dates
    private LocalDate quarter1PaymentDate;
    private LocalDate quarter2PaymentDate;
    private LocalDate quarter3PaymentDate;
    private LocalDate quarter4PaymentDate;
    
    private Double quarterlyAmount;
    
    // Contact Information
    private String contractorName;
    private String contractorContact;
    private String contractorEmail;
    
    @Column(length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    private LiftStatus status = LiftStatus.ACTIVE;
    
    // Relationships
    @OneToMany(mappedBy = "lift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceRecord> serviceRecords = new ArrayList<>();
    
    @OneToMany(mappedBy = "lift", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();
    
    // Audit fields
    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();
    
    private LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    public enum LiftStatus {
        ACTIVE, INACTIVE, UNDER_MAINTENANCE, DECOMMISSIONED
    }
}
