package com.vertitrack.controller;

import com.vertitrack.model.Employee;
import com.vertitrack.service.EmployeeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    // Form Fields
    @FXML private TextField employeeCodeField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField designationField;
    @FXML private TextField departmentField;
    @FXML private TextField contactNumberField;
    @FXML private TextField emailField;
    @FXML private TextArea addressArea;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField pincodeField;
    @FXML private DatePicker joiningDatePicker;
    @FXML private TextField salaryField;
    @FXML private ComboBox<Employee.EmployeeStatus> statusComboBox;
    @FXML private TextArea notesArea;
    
    // Table
    @FXML private TableView<Employee> employeesTable;
    @FXML private TableColumn<Employee, String> empCodeColumn;
    @FXML private TableColumn<Employee, String> nameColumn;
    @FXML private TableColumn<Employee, String> designationColumn;
    @FXML private TableColumn<Employee, String> contactColumn;
    @FXML private TableColumn<Employee, String> statusColumn;
    
    @FXML private TextField searchField;
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    @FXML private Button generateCodeButton;
    
    private ObservableList<Employee> employeesList = FXCollections.observableArrayList();
    private Employee selectedEmployee = null;
    
    @FXML
    public void initialize() {
        log.info("Initializing Employee Controller");
        
        setupStatusComboBox();
        setupTable();
        loadAllEmployees();
        setupEventHandlers();
    }
    
    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(Employee.EmployeeStatus.values()));
        statusComboBox.setValue(Employee.EmployeeStatus.ACTIVE);
    }
    
    private void setupTable() {
        empCodeColumn.setCellValueFactory(new PropertyValueFactory<>("employeeCode"));
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        designationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        employeesTable.setItems(employeesList);
        
        employeesTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadEmployeeToForm(newValue);
                }
            }
        );
    }
    
    private void loadAllEmployees() {
        try {
            List<Employee> employees = employeeService.findAllEmployees();
            employeesList.clear();
            employeesList.addAll(employees);
            log.info("Loaded {} employees", employees.size());
        } catch (Exception e) {
            log.error("Error loading employees", e);
            showError("Error loading employees: " + e.getMessage());
        }
    }
    
    private void setupEventHandlers() {
        saveButton.setOnAction(e -> saveEmployee());
        clearButton.setOnAction(e -> clearForm());
        deleteButton.setOnAction(e -> deleteEmployee());
        generateCodeButton.setOnAction(e -> generateEmployeeCode());
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchEmployees(newValue));
    }
    
    @FXML
    private void generateEmployeeCode() {
        String code = employeeService.generateEmployeeCode();
        employeeCodeField.setText(code);
    }
    
    @FXML
    private void saveEmployee() {
        try {
            if (!validateForm()) {
                return;
            }
            
            Employee employee;
            if (selectedEmployee != null) {
                employee = selectedEmployee;
            } else {
                employee = new Employee();
            }
            
            employee.setEmployeeCode(employeeCodeField.getText().trim());
            employee.setFirstName(firstNameField.getText().trim());
            employee.setLastName(lastNameField.getText().trim());
            employee.setDesignation(designationField.getText().trim());
            employee.setDepartment(departmentField.getText().trim());
            employee.setContactNumber(contactNumberField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setAddress(addressArea.getText().trim());
            employee.setCity(cityField.getText().trim());
            employee.setState(stateField.getText().trim());
            employee.setPincode(pincodeField.getText().trim());
            employee.setJoiningDate(joiningDatePicker.getValue());
            employee.setStatus(statusComboBox.getValue());
            employee.setNotes(notesArea.getText().trim());
            
            if (!salaryField.getText().isEmpty()) {
                employee.setSalary(Double.parseDouble(salaryField.getText()));
            }
            
            employeeService.saveEmployee(employee);
            
            showInfo("Success", "Employee saved successfully!");
            clearForm();
            loadAllEmployees();
            
        } catch (NumberFormatException e) {
            showError("Invalid salary format!");
        } catch (Exception e) {
            log.error("Error saving employee", e);
            showError("Error saving employee: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        if (employeeCodeField.getText().trim().isEmpty()) {
            showError("Employee Code is required!");
            return false;
        }
        if (firstNameField.getText().trim().isEmpty()) {
            showError("First Name is required!");
            return false;
        }
        if (designationField.getText().trim().isEmpty()) {
            showError("Designation is required!");
            return false;
        }
        if (contactNumberField.getText().trim().isEmpty()) {
            showError("Contact Number is required!");
            return false;
        }
        if (joiningDatePicker.getValue() == null) {
            showError("Joining Date is required!");
            return false;
        }
        return true;
    }
    
    @FXML
    private void clearForm() {
        selectedEmployee = null;
        
        employeeCodeField.clear();
        firstNameField.clear();
        lastNameField.clear();
        designationField.clear();
        departmentField.clear();
        contactNumberField.clear();
        emailField.clear();
        addressArea.clear();
        cityField.clear();
        stateField.clear();
        pincodeField.clear();
        joiningDatePicker.setValue(null);
        salaryField.clear();
        statusComboBox.setValue(Employee.EmployeeStatus.ACTIVE);
        notesArea.clear();
        
        employeesTable.getSelectionModel().clearSelection();
    }
    
    private void loadEmployeeToForm(Employee employee) {
        selectedEmployee = employee;
        
        employeeCodeField.setText(employee.getEmployeeCode());
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        designationField.setText(employee.getDesignation());
        departmentField.setText(employee.getDepartment());
        contactNumberField.setText(employee.getContactNumber());
        emailField.setText(employee.getEmail());
        addressArea.setText(employee.getAddress());
        cityField.setText(employee.getCity());
        stateField.setText(employee.getState());
        pincodeField.setText(employee.getPincode());
        joiningDatePicker.setValue(employee.getJoiningDate());
        salaryField.setText(employee.getSalary() != null ? employee.getSalary().toString() : "");
        statusComboBox.setValue(employee.getStatus());
        notesArea.setText(employee.getNotes());
    }
    
    @FXML
    private void deleteEmployee() {
        if (selectedEmployee == null) {
            showError("Please select an employee to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Employee: " + selectedEmployee.getFullName());
        confirmAlert.setContentText("Are you sure you want to delete this employee?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                employeeService.deleteEmployee(selectedEmployee.getId());
                showInfo("Success", "Employee deleted successfully!");
                clearForm();
                loadAllEmployees();
            } catch (Exception e) {
                log.error("Error deleting employee", e);
                showError("Error deleting employee: " + e.getMessage());
            }
        }
    }
    
    private void searchEmployees(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadAllEmployees();
            return;
        }
        
        try {
            List<Employee> results = employeeService.searchEmployees(keyword.trim());
            employeesList.clear();
            employeesList.addAll(results);
        } catch (Exception e) {
            log.error("Error searching employees", e);
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
