package com.vertitrack.repository;

import com.vertitrack.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmployeeCode(String employeeCode);
    
    Optional<Employee> findByContactNumber(String contactNumber);
    
    Optional<Employee> findByEmail(String email);
    
    List<Employee> findByStatus(Employee.EmployeeStatus status);
    
    List<Employee> findByDesignation(String designation);
    
    List<Employee> findByDepartment(String department);
    
    // Find all active employees
    @Query("SELECT e FROM Employee e WHERE e.status = 'ACTIVE' ORDER BY e.firstName, e.lastName")
    List<Employee> findAllActiveEmployees();
    
    // Count employees by status
    long countByStatus(Employee.EmployeeStatus status);
    
    // Search employees by multiple criteria
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.employeeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.contactNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Employee> searchEmployees(@Param("keyword") String keyword);
    
    // Find employees by joining date range
    @Query("SELECT e FROM Employee e WHERE e.joiningDate BETWEEN :startDate AND :endDate ORDER BY e.joiningDate")
    List<Employee> findByJoiningDateBetween(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    // Find employees who joined in a specific month/year
    @Query("SELECT e FROM Employee e WHERE YEAR(e.joiningDate) = :year AND MONTH(e.joiningDate) = :month")
    List<Employee> findByJoiningYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    // Find employees with relieving date (resigned/terminated)
    @Query("SELECT e FROM Employee e WHERE e.relievingDate IS NOT NULL ORDER BY e.relievingDate DESC")
    List<Employee> findRelievedEmployees();
}
