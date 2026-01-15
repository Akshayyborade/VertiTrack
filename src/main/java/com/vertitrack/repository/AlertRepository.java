package com.vertitrack.repository;

import com.vertitrack.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    // Find active alerts
    @Query("SELECT a FROM Alert a WHERE a.isActive = true ORDER BY a.priority, a.alertDate")
    List<Alert> findActiveAlerts();
    
    // Find unread alerts
    @Query("SELECT a FROM Alert a WHERE a.isRead = false AND a.isActive = true ORDER BY a.priority, a.alertDate")
    List<Alert> findUnreadAlerts();
    
    // Find alerts by priority
    @Query("SELECT a FROM Alert a WHERE a.priority = :priority AND a.isActive = true ORDER BY a.alertDate")
    List<Alert> findByPriority(@Param("priority") Alert.AlertPriority priority);
    
    // Find alerts by type
    @Query("SELECT a FROM Alert a WHERE a.alertType = :alertType AND a.isActive = true ORDER BY a.alertDate DESC")
    List<Alert> findByAlertType(@Param("alertType") Alert.AlertType alertType);
    
    // Find critical and high priority alerts
    @Query("SELECT a FROM Alert a WHERE (a.priority = 'CRITICAL' OR a.priority = 'HIGH') AND a.isActive = true AND a.isRead = false ORDER BY a.priority, a.alertDate")
    List<Alert> findCriticalAndHighPriorityAlerts();
    
    // Find alerts for a specific lift
    @Query("SELECT a FROM Alert a WHERE a.lift.id = :liftId AND a.isActive = true ORDER BY a.alertDate DESC")
    List<Alert> findByLiftId(@Param("liftId") Long liftId);
    
    // Find alerts for a specific employee
    @Query("SELECT a FROM Alert a WHERE a.employee.id = :employeeId AND a.isActive = true ORDER BY a.alertDate DESC")
    List<Alert> findByEmployeeId(@Param("employeeId") Long employeeId);
    
    // Find alerts due within date range
    @Query("SELECT a FROM Alert a WHERE a.dueDate BETWEEN :startDate AND :endDate AND a.isActive = true ORDER BY a.dueDate")
    List<Alert> findAlertsDueBetween(@Param("startDate") LocalDate startDate, 
                                     @Param("endDate") LocalDate endDate);
    
    // Count unread alerts
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.isRead = false AND a.isActive = true")
    long countUnreadAlerts();
    
    // Count alerts by priority
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.priority = :priority AND a.isActive = true")
    long countByPriority(@Param("priority") Alert.AlertPriority priority);
    
    // Delete old dismissed alerts (cleanup)
    @Query("SELECT a FROM Alert a WHERE a.isActive = false AND a.dismissedAt < :beforeDate")
    List<Alert> findOldDismissedAlerts(@Param("beforeDate") java.time.LocalDateTime beforeDate);
}
