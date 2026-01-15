package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String employeeCode;
    
    @Column(nullable = false)
    private String firstName;
    
    private String lastName;
    
    @Column(nullable = false)
    private String designation;
    
    private String department;
    
    @Column(nullable = false, unique = true)
    private String contactNumber;
    
    @Column(unique = true)
    private String email;
    
    private String address;
    
    private String city;
    
    private String state;
    
    private String pincode;
    
    @Column(nullable = false)
    private LocalDate joiningDate;
    
    private LocalDate relievingDate;
    
    private Double salary;
    
    @Enumerated(EnumType.STRING)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;
    
    // Emergency Contact
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyContactRelation;
    
    @Column(length = 1000)
    private String notes;
    
    // Relationships
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendanceRecords = new ArrayList<>();
    
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();
    
    // Audit fields
    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();
    
    private LocalDate updatedAt = LocalDate.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDate.now();
    }
    
    // Helper method to get full name
    @Transient
    public String getFullName() {
        return firstName + (lastName != null ? " " + lastName : "");
    }
    
    public enum EmployeeStatus {
        ACTIVE, INACTIVE, ON_LEAVE, RESIGNED, TERMINATED
    }
}
