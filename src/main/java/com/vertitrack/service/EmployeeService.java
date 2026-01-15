package com.vertitrack.service;

import com.vertitrack.model.Employee;
import com.vertitrack.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeService {
    
    private final EmployeeRepository employeeRepository;
    
    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }
    
    public Optional<Employee> findByEmployeeCode(String employeeCode) {
        return employeeRepository.findByEmployeeCode(employeeCode);
    }
    
    public Optional<Employee> findByContactNumber(String contactNumber) {
        return employeeRepository.findByContactNumber(contactNumber);
    }
    
    public Optional<Employee> findByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }
    
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public List<Employee> findAllActiveEmployees() {
        return employeeRepository.findAllActiveEmployees();
    }
    
    public List<Employee> findByStatus(Employee.EmployeeStatus status) {
        return employeeRepository.findByStatus(status);
    }
    
    public List<Employee> findByDesignation(String designation) {
        return employeeRepository.findByDesignation(designation);
    }
    
    public List<Employee> findByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }
    
    public List<Employee> searchEmployees(String keyword) {
        return employeeRepository.searchEmployees(keyword);
    }
    
    public List<Employee> findRelievedEmployees() {
        return employeeRepository.findRelievedEmployees();
    }
    
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
    
    // Statistics
    public long countActiveEmployees() {
        return employeeRepository.countByStatus(Employee.EmployeeStatus.ACTIVE);
    }
    
    public long countByStatus(Employee.EmployeeStatus status) {
        return employeeRepository.countByStatus(status);
    }
    
    // Business Logic
    public String generateEmployeeCode() {
        // Generate employee code like EMP001, EMP002, etc.
        long count = employeeRepository.count() + 1;
        return String.format("EMP%03d", count);
    }
    
    public boolean isEmployeeActive(Employee employee) {
        return employee.getStatus() == Employee.EmployeeStatus.ACTIVE;
    }
}
