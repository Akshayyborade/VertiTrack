package com.vertitrack.repository;

import com.vertitrack.model.Attendance;
import com.vertitrack.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    
    // Find attendance by employee and date
    Optional<Attendance> findByEmployeeAndAttendanceDate(Employee employee, LocalDate attendanceDate);
    
    // Find all attendance records for an employee
    List<Attendance> findByEmployeeOrderByAttendanceDateDesc(Employee employee);
    
    // Find attendance by employee ID
    List<Attendance> findByEmployeeIdOrderByAttendanceDateDesc(Long employeeId);
    
    // Find attendance by status
    List<Attendance> findByStatus(Attendance.AttendanceStatus status);
    
    // Find attendance between dates
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate DESC")
    List<Attendance> findAttendanceBetweenDates(@Param("startDate") LocalDate startDate, 
                                                 @Param("endDate") LocalDate endDate);
    
    // Find employee attendance between dates
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND a.attendanceDate BETWEEN :startDate AND :endDate ORDER BY a.attendanceDate")
    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(@Param("employeeId") Long employeeId, 
                                                               @Param("startDate") LocalDate startDate, 
                                                               @Param("endDate") LocalDate endDate);
    
    // Get monthly attendance for an employee
    @Query("SELECT a FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month ORDER BY a.attendanceDate")
    List<Attendance> getMonthlyAttendanceByEmployee(@Param("employeeId") Long employeeId, 
                                                     @Param("year") int year, 
                                                     @Param("month") int month);
    
    // Count present days for employee in a month
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.status = 'PRESENT'")
    long countPresentDaysByMonth(@Param("employeeId") Long employeeId, 
                                  @Param("year") int year, 
                                  @Param("month") int month);
    
    // Count absent days for employee in a month
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.status = 'ABSENT'")
    long countAbsentDaysByMonth(@Param("employeeId") Long employeeId, 
                                 @Param("year") int year, 
                                 @Param("month") int month);
    
    // Count leaves for employee in a month
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.status = 'LEAVE'")
    long countLeavesByMonth(@Param("employeeId") Long employeeId, 
                            @Param("year") int year, 
                            @Param("month") int month);
    
    // Get all absences for a date (for daily alert)
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate = :date AND a.status = 'ABSENT'")
    List<Attendance> findAbsenteesByDate(@Param("date") LocalDate date);
    
    // Get attendance summary by employee and status for a month
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month AND a.status = :status")
    long countByEmployeeMonthAndStatus(@Param("employeeId") Long employeeId, 
                                        @Param("year") int year, 
                                        @Param("month") int month, 
                                        @Param("status") Attendance.AttendanceStatus status);
    
    // Check if attendance exists for employee on a date
    boolean existsByEmployeeAndAttendanceDate(Employee employee, LocalDate attendanceDate);
    
    // Get total work hours for employee in a month
    @Query("SELECT COALESCE(SUM(a.workHours), 0) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month")
    Double getTotalWorkHoursByMonth(@Param("employeeId") Long employeeId, 
                                    @Param("year") int year, 
                                    @Param("month") int month);
    
    // Get overtime hours for employee in a month
    @Query("SELECT COALESCE(SUM(a.overtimeHours), 0) FROM Attendance a WHERE a.employee.id = :employeeId AND YEAR(a.attendanceDate) = :year AND MONTH(a.attendanceDate) = :month")
    Double getTotalOvertimeHoursByMonth(@Param("employeeId") Long employeeId, 
                                        @Param("year") int year, 
                                        @Param("month") int month);
}
