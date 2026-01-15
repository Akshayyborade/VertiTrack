package com.vertitrack.service;

import com.vertitrack.model.Attendance;
import com.vertitrack.model.Lift;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Reminder Service - Handles all automated alerts and reminders
 * Scheduled tasks run daily to check for:
 * 1. AMC Renewal & Expiry date alerts
 * 2. Quarterly Payment reminders
 * 3. Service due reminders
 * 4. Employee absence tracking
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderService {
    
    private final LiftService liftService;
    private final AlertService alertService;
    private final ServiceRecordService serviceRecordService;
    private final AttendanceService attendanceService;
    
    /**
     * Daily scheduled task - runs every day at 9:00 AM
     * Checks for all types of alerts and creates notifications
     */
    @Scheduled(cron = "0 0 9 * * *") // Every day at 9:00 AM
    public void checkDailyReminders() {
        log.info("Running daily reminder checks at {}", LocalDate.now());
        
        checkAmcExpiryAlerts();
        checkQuarterlyPaymentAlerts();
        checkServiceDueAlerts();
        
        log.info("Daily reminder check completed");
    }
    
    /**
     * Check AMC Expiry Alerts
     * Creates alerts for lifts with AMC expiring in 30, 15, and 7 days
     */
    public void checkAmcExpiryAlerts() {
        log.info("Checking AMC expiry alerts...");
        
        LocalDate today = LocalDate.now();
        
        // Check for lifts with AMC expiring in next 30 days
        List<Lift> liftsExpiringIn30Days = liftService.findLiftsWithAmcExpiringInDays(30);
        
        for (Lift lift : liftsExpiringIn30Days) {
            long daysUntilExpiry = ChronoUnit.DAYS.between(today, lift.getAmcEndDate());
            
            // Create alert for critical milestones: 30, 15, 7 days
            if (daysUntilExpiry == 30 || daysUntilExpiry == 15 || daysUntilExpiry == 7 || daysUntilExpiry <= 3) {
                alertService.createAmcExpiryAlert(lift, (int) daysUntilExpiry);
                log.info("Created AMC expiry alert for lift {} - {} days remaining", 
                    lift.getLiftNumber(), daysUntilExpiry);
            }
        }
        
        // Check for expired AMC
        List<Lift> expiredAmcLifts = liftService.findLiftsWithExpiredAmc();
        for (Lift lift : expiredAmcLifts) {
            alertService.createAmcExpiryAlert(lift, 0);
            log.warn("AMC EXPIRED for lift {} at {}", lift.getLiftNumber(), lift.getLocation());
        }
        
        log.info("AMC expiry check completed. Checked {} lifts", liftsExpiringIn30Days.size());
    }
    
    /**
     * Check Quarterly Payment Alerts
     * Creates alerts for quarterly payments due in next 15 days
     */
    public void checkQuarterlyPaymentAlerts() {
        log.info("Checking quarterly payment alerts...");
        
        LocalDate today = LocalDate.now();
        List<Lift> allActiveLifts = liftService.findAllActiveLifts();
        
        for (Lift lift : allActiveLifts) {
            // Check all four quarters
            checkQuarterPayment(lift, lift.getQuarter1PaymentDate(), "Quarter 1", today);
            checkQuarterPayment(lift, lift.getQuarter2PaymentDate(), "Quarter 2", today);
            checkQuarterPayment(lift, lift.getQuarter3PaymentDate(), "Quarter 3", today);
            checkQuarterPayment(lift, lift.getQuarter4PaymentDate(), "Quarter 4", today);
        }
        
        log.info("Quarterly payment check completed");
    }
    
    private void checkQuarterPayment(Lift lift, LocalDate paymentDate, String quarter, LocalDate today) {
        if (paymentDate != null) {
            long daysUntilPayment = ChronoUnit.DAYS.between(today, paymentDate);
            
            // Alert 15, 7, and 3 days before payment due
            if (daysUntilPayment >= 0 && (daysUntilPayment == 15 || daysUntilPayment == 7 || daysUntilPayment == 3 || daysUntilPayment == 0)) {
                alertService.createQuarterlyPaymentAlert(lift, paymentDate, quarter);
                log.info("Created {} payment alert for lift {} - {} days remaining", 
                    quarter, lift.getLiftNumber(), daysUntilPayment);
            }
        }
    }
    
    /**
     * Check Service Due Alerts
     * Creates alerts for overdue and upcoming services
     */
    public void checkServiceDueAlerts() {
        log.info("Checking service due alerts...");
        
        // Find overdue services
        var overdueServices = serviceRecordService.findOverdueServices();
        log.info("Found {} overdue services", overdueServices.size());
        
        // Find upcoming services (due in next 15 days)
        var upcomingServices = serviceRecordService.findUpcomingServiceDue(15);
        log.info("Found {} upcoming services", upcomingServices.size());
        
        // Can create alerts for these if needed
        // For now, they will be visible in the dashboard
        
        log.info("Service due check completed");
    }
    
    /**
     * Optional: Track employee absences
     * This can be called after attendance is marked for the day
     */
    public void checkEmployeeAbsences(LocalDate date) {
        log.info("Checking employee absences for {}", date);
        
        List<Attendance> absentees = attendanceService.findAbsenteesByDate(date);
        
        for (Attendance attendance : absentees) {
            alertService.createEmployeeAbsenceAlert(attendance.getEmployee(), date);
            log.info("Employee {} was absent on {}", 
                attendance.getEmployee().getFullName(), date);
        }
        
        log.info("Found {} absent employees on {}", absentees.size(), date);
    }
    
    /**
     * Manual check for immediate alerts (useful for dashboard refresh)
     */
    public void runManualCheck() {
        log.info("Running manual reminder check");
        checkAmcExpiryAlerts();
        checkQuarterlyPaymentAlerts();
        checkServiceDueAlerts();
    }
    
    /**
     * Cleanup old dismissed alerts - runs monthly
     */
    @Scheduled(cron = "0 0 2 1 * *") // 1st day of every month at 2:00 AM
    public void cleanupOldAlerts() {
        log.info("Cleaning up old dismissed alerts");
        alertService.cleanupOldDismissedAlerts(90); // Delete alerts older than 90 days
        log.info("Old alerts cleanup completed");
    }
}
