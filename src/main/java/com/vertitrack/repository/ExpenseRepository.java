package com.vertitrack.repository;

import com.vertitrack.model.Expense;
import com.vertitrack.model.Employee;
import com.vertitrack.model.Lift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    // Find by expense type
    List<Expense> findByExpenseType(Expense.ExpenseType expenseType);
    
    // Find by category
    List<Expense> findByCategory(String category);
    
    // Find by payment status
    List<Expense> findByPaymentStatus(Expense.PaymentStatus paymentStatus);
    
    // Find by lift
    List<Expense> findByLiftOrderByExpenseDateDesc(Lift lift);
    
    // Find by employee
    List<Expense> findByEmployeeOrderByExpenseDateDesc(Employee employee);
    
    // Find expenses between dates
    @Query("SELECT e FROM Expense e WHERE e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    List<Expense> findExpensesBetweenDates(@Param("startDate") LocalDate startDate, 
                                           @Param("endDate") LocalDate endDate);
    
    // Find expenses for a lift between dates
    @Query("SELECT e FROM Expense e WHERE e.lift.id = :liftId AND e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    List<Expense> findByLiftIdAndExpenseDateBetween(@Param("liftId") Long liftId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);
    
    // Find employee expenses between dates
    @Query("SELECT e FROM Expense e WHERE e.employee.id = :employeeId AND e.expenseDate BETWEEN :startDate AND :endDate ORDER BY e.expenseDate DESC")
    List<Expense> findByEmployeeIdAndExpenseDateBetween(@Param("employeeId") Long employeeId, 
                                                         @Param("startDate") LocalDate startDate, 
                                                         @Param("endDate") LocalDate endDate);
    
    // Get total expense for a lift in a year (AMC & Repairing Payment Record)
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.lift.id = :liftId AND YEAR(e.expenseDate) = :year")
    Double getTotalExpenseByLiftAndYear(@Param("liftId") Long liftId, @Param("year") int year);
    
    // Get total expense by type for a lift in a year
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.lift.id = :liftId AND e.expenseType = :expenseType AND YEAR(e.expenseDate) = :year")
    Double getTotalExpenseByLiftTypeAndYear(@Param("liftId") Long liftId, 
                                            @Param("expenseType") Expense.ExpenseType expenseType, 
                                            @Param("year") int year);
    
    // Get AMC material expenses for a year
    @Query("SELECT e FROM Expense e WHERE e.expenseType = 'MATERIAL_EXPENSE' AND YEAR(e.expenseDate) = :year ORDER BY e.expenseDate DESC")
    List<Expense> getAmcMaterialExpensesByYear(@Param("year") int year);
    
    // Get employee petrol expenses
    @Query("SELECT e FROM Expense e WHERE e.expenseType = 'EMPLOYEE_PETROL' ORDER BY e.expenseDate DESC")
    List<Expense> getEmployeePetrolExpenses();
    
    // Get employee other expenses
    @Query("SELECT e FROM Expense e WHERE e.expenseType = 'EMPLOYEE_OTHER' ORDER BY e.expenseDate DESC")
    List<Expense> getEmployeeOtherExpenses();
    
    // Get employee expenses for a specific month
    @Query("SELECT e FROM Expense e WHERE e.employee.id = :employeeId AND YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month ORDER BY e.expenseDate DESC")
    List<Expense> getEmployeeExpensesByMonth(@Param("employeeId") Long employeeId, 
                                             @Param("year") int year, 
                                             @Param("month") int month);
    
    // Get total employee expenses for a month
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.employee.id = :employeeId AND YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month")
    Double getTotalEmployeeExpensesByMonth(@Param("employeeId") Long employeeId, 
                                           @Param("year") int year, 
                                           @Param("month") int month);
    
    // Get overdue payments
    @Query("SELECT e FROM Expense e WHERE e.paymentStatus = 'OVERDUE' OR e.paymentStatus = 'PENDING' ORDER BY e.expenseDate")
    List<Expense> getOverduePayments();
    
    // Get total expense by type
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseType = :expenseType")
    Double getTotalExpenseByType(@Param("expenseType") Expense.ExpenseType expenseType);
    
    // Get monthly total expenses
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE YEAR(e.expenseDate) = :year AND MONTH(e.expenseDate) = :month")
    Double getMonthlyTotalExpenses(@Param("year") int year, @Param("month") int month);
    
    // Get yearly total expenses
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE YEAR(e.expenseDate) = :year")
    Double getYearlyTotalExpenses(@Param("year") int year);
    
    // Search expenses
    @Query("SELECT e FROM Expense e WHERE " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.paidTo) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.invoiceNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Expense> searchExpenses(@Param("keyword") String keyword);
}
