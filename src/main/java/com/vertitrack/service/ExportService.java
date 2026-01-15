package com.vertitrack.service;

import com.vertitrack.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Export Service - Handles data export functionality
 * Exports to CSV format for easy viewing in Excel
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {
    
    private final LiftService liftService;
    private final ServiceRecordService serviceRecordService;
    private final ExpenseService expenseService;
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    /**
     * Export all lifts to CSV
     */
    public Path exportLiftsToCSV(String filePath) throws IOException {
        List<Lift> lifts = liftService.findAllLifts();
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Header
            writer.write("Lift Number,Location,Building,Type,Capacity,AMC Start Date,AMC End Date,AMC Renewal Date,AMC Amount,Contractor Name,Status\n");
            
            // Data
            for (Lift lift : lifts) {
                writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%.2f,%s,%s\n",
                    escapeCSV(lift.getLiftNumber()),
                    escapeCSV(lift.getLocation()),
                    escapeCSV(lift.getBuilding()),
                    escapeCSV(lift.getLiftType()),
                    lift.getCapacity(),
                    formatDate(lift.getAmcStartDate()),
                    formatDate(lift.getAmcEndDate()),
                    formatDate(lift.getAmcRenewalDate()),
                    lift.getAmcAmount(),
                    escapeCSV(lift.getContractorName()),
                    lift.getStatus()
                ));
            }
        }
        
        log.info("Exported {} lifts to {}", lifts.size(), filePath);
        return path;
    }
    
    /**
     * Export service records for a lift (whole year)
     */
    public Path exportLiftServiceRecordsToCSV(Long liftId, int year, String filePath) throws IOException {
        List<ServiceRecord> servicingRecords = serviceRecordService.findAmcServicingRecordsByLiftAndYear(liftId, year);
        List<ServiceRecord> repairRecords = serviceRecordService.findAmcRepairingRecordsByLiftAndYear(liftId, year);
        
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Header
            writer.write("Service Type,Service Date,Performed By,Work Description,Labor Cost,Parts Cost,Total Cost,Status,Invoice Number\n");
            
            // Servicing Records
            writer.write("\n=== AMC SERVICING RECORDS ===\n");
            for (ServiceRecord record : servicingRecords) {
                writeServiceRecord(writer, record);
            }
            
            // Repair Records
            writer.write("\n=== AMC REPAIR RECORDS ===\n");
            for (ServiceRecord record : repairRecords) {
                writeServiceRecord(writer, record);
            }
            
            // Total
            Double totalCost = serviceRecordService.getTotalServiceCostByLiftAndYear(liftId, year);
            writer.write(String.format("\nTotal Service Cost for Year %d: ₹%.2f\n", year, totalCost));
        }
        
        log.info("Exported service records for lift {} year {} to {}", liftId, year, filePath);
        return path;
    }
    
    /**
     * Export expense report for a year
     */
    public Path exportYearlyExpenseReportToCSV(int year, String filePath) throws IOException {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        
        List<Expense> expenses = expenseService.findExpensesBetweenDates(startDate, endDate);
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Header
            writer.write("Date,Type,Category,Amount,Paid To,Lift,Employee,Description,Invoice Number,Payment Status\n");
            
            // Data
            for (Expense expense : expenses) {
                writer.write(String.format("%s,%s,%s,%.2f,%s,%s,%s,%s,%s,%s\n",
                    formatDate(expense.getExpenseDate()),
                    expense.getExpenseType(),
                    escapeCSV(expense.getCategory()),
                    expense.getAmount(),
                    escapeCSV(expense.getPaidTo()),
                    expense.getLift() != null ? escapeCSV(expense.getLift().getLiftNumber()) : "",
                    expense.getEmployee() != null ? escapeCSV(expense.getEmployee().getFullName()) : "",
                    escapeCSV(expense.getDescription()),
                    escapeCSV(expense.getInvoiceNumber()),
                    expense.getPaymentStatus()
                ));
            }
            
            // Summary
            writer.write("\n=== EXPENSE SUMMARY ===\n");
            writer.write(String.format("AMC Payments: ₹%.2f\n", 
                expenseService.getTotalExpenseByType(Expense.ExpenseType.AMC_PAYMENT)));
            writer.write(String.format("Repair Payments: ₹%.2f\n", 
                expenseService.getTotalExpenseByType(Expense.ExpenseType.REPAIR_PAYMENT)));
            writer.write(String.format("Material Expenses: ₹%.2f\n", 
                expenseService.getTotalExpenseByType(Expense.ExpenseType.MATERIAL_EXPENSE)));
            writer.write(String.format("Employee Petrol: ₹%.2f\n", 
                expenseService.getTotalExpenseByType(Expense.ExpenseType.EMPLOYEE_PETROL)));
            writer.write(String.format("Employee Other: ₹%.2f\n", 
                expenseService.getTotalExpenseByType(Expense.ExpenseType.EMPLOYEE_OTHER)));
            writer.write(String.format("\nTotal Yearly Expenses: ₹%.2f\n", 
                expenseService.getYearlyTotalExpenses(year)));
        }
        
        log.info("Exported yearly expense report for {} to {}", year, filePath);
        return path;
    }
    
    /**
     * Export employee attendance for a month
     */
    public Path exportMonthlyAttendanceToCSV(Long employeeId, int year, int month, String filePath) throws IOException {
        List<Attendance> attendanceList = attendanceService.getMonthlyAttendanceByEmployee(employeeId, year, month);
        Map<String, Long> summary = attendanceService.getMonthlyAttendanceSummary(employeeId, year, month);
        
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Header
            writer.write("Date,Status,Check In,Check Out,Work Hours,Overtime Hours,Leave Type,Remarks\n");
            
            // Data
            for (Attendance attendance : attendanceList) {
                writer.write(String.format("%s,%s,%s,%s,%.2f,%.2f,%s,%s\n",
                    formatDate(attendance.getAttendanceDate()),
                    attendance.getStatus(),
                    attendance.getCheckInTime() != null ? attendance.getCheckInTime().toString() : "",
                    attendance.getCheckOutTime() != null ? attendance.getCheckOutTime().toString() : "",
                    attendance.getWorkHours() != null ? attendance.getWorkHours() : 0.0,
                    attendance.getOvertimeHours() != null ? attendance.getOvertimeHours() : 0.0,
                    attendance.getLeaveType() != null ? attendance.getLeaveType().toString() : "",
                    escapeCSV(attendance.getRemarks())
                ));
            }
            
            // Summary
            writer.write("\n=== ATTENDANCE SUMMARY ===\n");
            writer.write(String.format("Present Days: %d\n", summary.get("present")));
            writer.write(String.format("Absent Days: %d\n", summary.get("absent")));
            writer.write(String.format("Leave Days: %d\n", summary.get("leaves")));
            writer.write(String.format("Half Days: %d\n", summary.get("halfDay")));
            writer.write(String.format("Holidays: %d\n", summary.get("holiday")));
            writer.write(String.format("Week Offs: %d\n", summary.get("weekOff")));
            
            Double totalWorkHours = attendanceService.getTotalWorkHoursByMonth(employeeId, year, month);
            Double totalOvertimeHours = attendanceService.getTotalOvertimeHoursByMonth(employeeId, year, month);
            writer.write(String.format("\nTotal Work Hours: %.2f\n", totalWorkHours));
            writer.write(String.format("Total Overtime Hours: %.2f\n", totalOvertimeHours));
        }
        
        log.info("Exported monthly attendance for employee {} to {}", employeeId, filePath);
        return path;
    }
    
    /**
     * Export employee expenses for a month
     */
    public Path exportEmployeeMonthlyExpensesToCSV(Long employeeId, int year, int month, String filePath) throws IOException {
        List<Expense> expenses = expenseService.getEmployeeExpensesByMonth(employeeId, year, month);
        Path path = Paths.get(filePath);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            // Header
            writer.write("Date,Type,Category,Amount,Description,Payment Mode,Status\n");
            
            // Data
            for (Expense expense : expenses) {
                writer.write(String.format("%s,%s,%s,%.2f,%s,%s,%s\n",
                    formatDate(expense.getExpenseDate()),
                    expense.getExpenseType(),
                    escapeCSV(expense.getCategory()),
                    expense.getAmount(),
                    escapeCSV(expense.getDescription()),
                    escapeCSV(expense.getPaymentMode()),
                    expense.getPaymentStatus()
                ));
            }
            
            // Total
            Double total = expenseService.getTotalEmployeeExpensesByMonth(employeeId, year, month);
            writer.write(String.format("\nTotal Expenses: ₹%.2f\n", total));
        }
        
        log.info("Exported employee monthly expenses for employee {} to {}", employeeId, filePath);
        return path;
    }
    
    // Helper methods
    private void writeServiceRecord(BufferedWriter writer, ServiceRecord record) throws IOException {
        writer.write(String.format("%s,%s,%s,%s,%.2f,%.2f,%.2f,%s,%s\n",
            record.getServiceType(),
            formatDate(record.getServiceDate()),
            escapeCSV(record.getPerformedBy()),
            escapeCSV(record.getWorkDescription()),
            record.getLaborCost() != null ? record.getLaborCost() : 0.0,
            record.getPartsCost() != null ? record.getPartsCost() : 0.0,
            record.getTotalCost() != null ? record.getTotalCost() : 0.0,
            record.getStatus(),
            escapeCSV(record.getInvoiceNumber())
        ));
    }
    
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }
    
    private String escapeCSV(String value) {
        if (value == null) return "";
        // Escape commas and quotes for CSV
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
