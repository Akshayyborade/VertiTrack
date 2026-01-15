package com.vertitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "attendance_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttendanceStatus status;
    
    private LocalTime checkInTime;
    
    private LocalTime checkOutTime;
    
    private Double workHours;
    
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    
    @Column(length = 500)
    private String remarks;
    
    // For overtime tracking
    private Double overtimeHours;
    
    // Audit fields
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum AttendanceStatus {
        PRESENT, 
        ABSENT, 
        HALF_DAY, 
        LEAVE, 
        HOLIDAY, 
        WEEK_OFF,
        ON_DUTY,  // Working outside office
        WORK_FROM_HOME
    }
    
    public enum LeaveType {
        SICK_LEAVE,
        CASUAL_LEAVE,
        PAID_LEAVE,
        UNPAID_LEAVE,
        COMPENSATORY_OFF,
        MATERNITY_LEAVE,
        PATERNITY_LEAVE,
        OTHER
    }
}
