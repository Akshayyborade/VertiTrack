package com.vertitrack.service;

import com.vertitrack.model.Employee;
import com.vertitrack.model.Expense;
import com.vertitrack.model.Lift;
import com.vertitrack.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    
    public Expense saveExpense(Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }
    
    public List<Expense> findAllExpenses() {
        return expenseRepository.findAll();
    }
    
    public List<Expense> findByExpenseType(Expense.ExpenseType expenseType) {
        return expenseRepository.findByExpenseType(expenseType);
    }
    
    public List<Expense> findByCategory(String category) {
        return expenseRepository.findByCategory(category);
    }
    
    public List<Expense> findByPaymentStatus(Expense.PaymentStatus paymentStatus) {
        return expenseRepository.findByPaymentStatus(paymentStatus);
    }
    
    public List<Expense> findByLift(Lift lift) {
        return expenseRepository.findByLiftOrderByExpenseDateDesc(lift);
    }
    
    public List<Expense> findByEmployee(Employee employee) {
        return expenseRepository.findByEmployeeOrderByExpenseDateDesc(employee);
    }
    
    public List<Expense> findExpensesBetweenDates(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpensesBetweenDates(startDate, endDate);
    }
    
    public List<Expense> findByLiftIdAndDateRange(Long liftId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByLiftIdAndExpenseDateBetween(liftId, startDate, endDate);
    }
    
    public List<Expense> findByEmployeeIdAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByEmployeeIdAndExpenseDateBetween(employeeId, startDate, endDate);
    }
    
    // AMC & Repairing Payment Record - For Every Year
    public Double getTotalExpenseByLiftAndYear(Long liftId, int year) {
        return expenseRepository.getTotalExpenseByLiftAndYear(liftId, year);
    }
    
    public Double getTotalExpenseByLiftTypeAndYear(Long liftId, Expense.ExpenseType expenseType, int year) {
        return expenseRepository.getTotalExpenseByLiftTypeAndYear(liftId, expenseType, year);
    }
    
    // AMC Personal Material Expense Record
    public List<Expense> getAmcMaterialExpensesByYear(int year) {
        return expenseRepository.getAmcMaterialExpensesByYear(year);
    }
    
    // Employee Petrol & Other Expense Record
    public List<Expense> getEmployeePetrolExpenses() {
        return expenseRepository.getEmployeePetrolExpenses();
    }
    
    public List<Expense> getEmployeeOtherExpenses() {
        return expenseRepository.getEmployeeOtherExpenses();
    }
    
    public List<Expense> getEmployeeExpensesByMonth(Long employeeId, int year, int month) {
        return expenseRepository.getEmployeeExpensesByMonth(employeeId, year, month);
    }
    
    public Double getTotalEmployeeExpensesByMonth(Long employeeId, int year, int month) {
        return expenseRepository.getTotalEmployeeExpensesByMonth(employeeId, year, month);
    }
    
    // Payment tracking
    public List<Expense> getOverduePayments() {
        return expenseRepository.getOverduePayments();
    }
    
    // Statistics
    public Double getTotalExpenseByType(Expense.ExpenseType expenseType) {
        return expenseRepository.getTotalExpenseByType(expenseType);
    }
    
    public Double getMonthlyTotalExpenses(int year, int month) {
        return expenseRepository.getMonthlyTotalExpenses(year, month);
    }
    
    public Double getYearlyTotalExpenses(int year) {
        return expenseRepository.getYearlyTotalExpenses(year);
    }
    
    public List<Expense> searchExpenses(String keyword) {
        return expenseRepository.searchExpenses(keyword);
    }
    
    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }
    
    // Business Logic
    public void markAsOverdue(Expense expense) {
        expense.setPaymentStatus(Expense.PaymentStatus.OVERDUE);
        expenseRepository.save(expense);
    }
}
