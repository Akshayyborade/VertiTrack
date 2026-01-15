package com.vertitrack.service;

import com.vertitrack.model.Attendance;
import com.vertitrack.model.Employee;
import com.vertitrack.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    public Attendance saveAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }
    
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }
    
    public Optional<Attendance> findByEmployeeAndDate(Employee employee, LocalDate date) {
        return attendanceRepository.findByEmployeeAndAttendanceDate(employee, date);
    }
    
    public List<Attendance> findAllAttendance() {
        return attendanceRepository.findAll();
    }
    
    public List<Attendance> findByEmployee(Employee employee) {
        return attendanceRepository.findByEmployeeOrderByAttendanceDateDesc(employee);
    }
    
    public List<Attendance> findByEmployeeId(Long employeeId) {
        return attendanceRepository.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);
    }
    
    public List<Attendance> findByStatus(Attendance.AttendanceStatus status) {
        return attendanceRepository.findByStatus(status);
    }
    
    public List<Attendance> findAttendanceBetweenDates(LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findAttendanceBetweenDates(startDate, endDate);
    }
    
    public List<Attendance> findByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
    }
    
    // Employee Absence & Present Monthly Record
    public List<Attendance> getMonthlyAttendanceByEmployee(Long employeeId, int year, int month) {
        return attendanceRepository.getMonthlyAttendanceByEmployee(employeeId, year, month);
    }
    
    public long countPresentDaysByMonth(Long employeeId, int year, int month) {
        return attendanceRepository.countPresentDaysByMonth(employeeId, year, month);
    }
    
    public long countAbsentDaysByMonth(Long employeeId, int year, int month) {
        return attendanceRepository.countAbsentDaysByMonth(employeeId, year, month);
    }
    
    public long countLeavesByMonth(Long employeeId, int year, int month) {
        return attendanceRepository.countLeavesByMonth(employeeId, year, month);
    }
    
    // Get complete monthly summary
    public Map<String, Long> getMonthlyAttendanceSummary(Long employeeId, int year, int month) {
        Map<String, Long> summary = new HashMap<>();
        summary.put("present", countPresentDaysByMonth(employeeId, year, month));
        summary.put("absent", countAbsentDaysByMonth(employeeId, year, month));
        summary.put("leaves", countLeavesByMonth(employeeId, year, month));
        summary.put("halfDay", attendanceRepository.countByEmployeeMonthAndStatus(
            employeeId, year, month, Attendance.AttendanceStatus.HALF_DAY));
        summary.put("holiday", attendanceRepository.countByEmployeeMonthAndStatus(
            employeeId, year, month, Attendance.AttendanceStatus.HOLIDAY));
        summary.put("weekOff", attendanceRepository.countByEmployeeMonthAndStatus(
            employeeId, year, month, Attendance.AttendanceStatus.WEEK_OFF));
        return summary;
    }
    
    // Find daily absentees
    public List<Attendance> findAbsenteesByDate(LocalDate date) {
        return attendanceRepository.findAbsenteesByDate(date);
    }
    
    // Work hours tracking
    public Double getTotalWorkHoursByMonth(Long employeeId, int year, int month) {
        return attendanceRepository.getTotalWorkHoursByMonth(employeeId, year, month);
    }
    
    public Double getTotalOvertimeHoursByMonth(Long employeeId, int year, int month) {
        return attendanceRepository.getTotalOvertimeHoursByMonth(employeeId, year, month);
    }
    
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }
    
    // Business Logic
    public boolean isAttendanceMarked(Employee employee, LocalDate date) {
        return attendanceRepository.existsByEmployeeAndAttendanceDate(employee, date);
    }
    
    public void markAttendance(Employee employee, LocalDate date, Attendance.AttendanceStatus status) {
        if (!isAttendanceMarked(employee, date)) {
            Attendance attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setAttendanceDate(date);
            attendance.setStatus(status);
            attendanceRepository.save(attendance);
        }
    }
    
    // Calculate working days in a month
    public int getWorkingDaysInMonth(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        int workingDays = 0;
        
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = LocalDate.of(year, month, day);
            // Exclude Sundays (assuming Sunday is week off)
            if (date.getDayOfWeek().getValue() != 7) {
                workingDays++;
            }
        }
        return workingDays;
    }
}
