package com.vertitrack.service;

import com.vertitrack.model.Alert;
import com.vertitrack.model.Employee;
import com.vertitrack.model.Lift;
import com.vertitrack.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {
    
    private final AlertRepository alertRepository;
    
    public Alert saveAlert(Alert alert) {
        return alertRepository.save(alert);
    }
    
    public Optional<Alert> findById(Long id) {
        return alertRepository.findById(id);
    }
    
    public List<Alert> findAllActiveAlerts() {
        return alertRepository.findActiveAlerts();
    }
    
    public List<Alert> findUnreadAlerts() {
        return alertRepository.findUnreadAlerts();
    }
    
    public List<Alert> findByPriority(Alert.AlertPriority priority) {
        return alertRepository.findByPriority(priority);
    }
    
    public List<Alert> findByAlertType(Alert.AlertType alertType) {
        return alertRepository.findByAlertType(alertType);
    }
    
    public List<Alert> findCriticalAndHighPriorityAlerts() {
        return alertRepository.findCriticalAndHighPriorityAlerts();
    }
    
    public List<Alert> findByLiftId(Long liftId) {
        return alertRepository.findByLiftId(liftId);
    }
    
    public List<Alert> findByEmployeeId(Long employeeId) {
        return alertRepository.findByEmployeeId(employeeId);
    }
    
    public long countUnreadAlerts() {
        return alertRepository.countUnreadAlerts();
    }
    
    public long countByPriority(Alert.AlertPriority priority) {
        return alertRepository.countByPriority(priority);
    }
    
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
    
    // Business Logic
    public void markAsRead(Long alertId) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setIsRead(true);
            alert.setReadAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
    }
    
    public void dismissAlert(Long alertId, String action) {
        Optional<Alert> alertOpt = alertRepository.findById(alertId);
        if (alertOpt.isPresent()) {
            Alert alert = alertOpt.get();
            alert.setIsActive(false);
            alert.setDismissedAt(LocalDateTime.now());
            alert.setActionTaken(action);
            alertRepository.save(alert);
        }
    }
    
    // Create specific alert types
    public Alert createAmcExpiryAlert(Lift lift, int daysUntilExpiry) {
        Alert alert = new Alert();
        alert.setAlertType(Alert.AlertType.AMC_EXPIRY);
        alert.setLift(lift);
        alert.setAlertDate(LocalDate.now());
        alert.setDueDate(lift.getAmcEndDate());
        alert.setTitle("AMC Expiring Soon - " + lift.getLiftNumber());
        alert.setMessage(String.format("AMC for lift %s at %s is expiring in %d days on %s", 
            lift.getLiftNumber(), lift.getLocation(), daysUntilExpiry, lift.getAmcEndDate()));
        
        // Set priority based on days until expiry
        if (daysUntilExpiry <= 7) {
            alert.setPriority(Alert.AlertPriority.CRITICAL);
        } else if (daysUntilExpiry <= 15) {
            alert.setPriority(Alert.AlertPriority.HIGH);
        } else if (daysUntilExpiry <= 30) {
            alert.setPriority(Alert.AlertPriority.MEDIUM);
        } else {
            alert.setPriority(Alert.AlertPriority.LOW);
        }
        
        return alertRepository.save(alert);
    }
    
    public Alert createQuarterlyPaymentAlert(Lift lift, LocalDate paymentDate, String quarter) {
        Alert alert = new Alert();
        alert.setAlertType(Alert.AlertType.QUARTERLY_PAYMENT);
        alert.setLift(lift);
        alert.setAlertDate(LocalDate.now());
        alert.setDueDate(paymentDate);
        alert.setTitle("Quarterly Payment Due - " + lift.getLiftNumber());
        alert.setMessage(String.format("%s payment for lift %s at %s is due on %s. Amount: ₹%.2f", 
            quarter, lift.getLiftNumber(), lift.getLocation(), paymentDate, lift.getQuarterlyAmount()));
        
        long daysUntilPayment = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), paymentDate);
        if (daysUntilPayment <= 7) {
            alert.setPriority(Alert.AlertPriority.CRITICAL);
        } else if (daysUntilPayment <= 15) {
            alert.setPriority(Alert.AlertPriority.HIGH);
        } else {
            alert.setPriority(Alert.AlertPriority.MEDIUM);
        }
        
        return alertRepository.save(alert);
    }
    
    public Alert createEmployeeAbsenceAlert(Employee employee, LocalDate date) {
        Alert alert = new Alert();
        alert.setAlertType(Alert.AlertType.EMPLOYEE_ABSENCE);
        alert.setEmployee(employee);
        alert.setAlertDate(date);
        alert.setDueDate(date);
        alert.setTitle("Employee Absent - " + employee.getFullName());
        alert.setMessage(String.format("Employee %s (%s) was absent on %s", 
            employee.getFullName(), employee.getEmployeeCode(), date));
        alert.setPriority(Alert.AlertPriority.LOW);
        
        return alertRepository.save(alert);
    }
    
    // Cleanup old dismissed alerts (can be scheduled)
    public void cleanupOldDismissedAlerts(int daysOld) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(daysOld);
        List<Alert> oldAlerts = alertRepository.findOldDismissedAlerts(beforeDate);
        alertRepository.deleteAll(oldAlerts);
    }
}
