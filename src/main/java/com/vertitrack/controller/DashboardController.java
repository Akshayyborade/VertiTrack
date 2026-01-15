package com.vertitrack.controller;

import com.vertitrack.config.SpringFXMLLoader;
import com.vertitrack.model.Alert;
import com.vertitrack.model.Lift;
import com.vertitrack.service.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    
    private final LiftService liftService;
    private final ServiceRecordService serviceRecordService;
    private final ExpenseService expenseService;
    private final EmployeeService employeeService;
    private final AlertService alertService;
    private final ReminderService reminderService;
    private final SpringFXMLLoader fxmlLoader;
    
    // Dashboard Statistics
    @FXML private Label totalLiftsLabel;
    @FXML private Label activeLiftsLabel;
    @FXML private Label totalEmployeesLabel;
    @FXML private Label monthlyExpensesLabel;
    
    // Alert Table
    @FXML private TableView<Alert> alertsTable;
    @FXML private TableColumn<Alert, String> priorityColumn;
    @FXML private TableColumn<Alert, String> typeColumn;
    @FXML private TableColumn<Alert, String> titleColumn;
    @FXML private TableColumn<Alert, String> messageColumn;
    @FXML private TableColumn<Alert, LocalDate> dateColumn;
    
    // Buttons and Menu
    @FXML private Button refreshButton;
    @FXML private Button manageLiftsButton;
    @FXML private Button manageEmployeesButton;
    @FXML private Button serviceRecordsButton;
    @FXML private Button expenseRecordsButton;
    @FXML private Button attendanceButton;
    @FXML private Button reportsButton;
    
    private ObservableList<Alert> alertsList = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        log.info("Initializing Dashboard Controller");
        
        setupAlertTable();
        loadDashboardData();
        setupEventHandlers();
    }
    
    private void setupAlertTable() {
        // Configure alert table columns
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("alertType"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("alertDate"));
        
        // Set column widths
        priorityColumn.setPrefWidth(80);
        typeColumn.setPrefWidth(120);
        titleColumn.setPrefWidth(200);
        messageColumn.setPrefWidth(350);
        dateColumn.setPrefWidth(100);
        
        // Color code by priority
        priorityColumn.setCellFactory(column -> new TableCell<Alert, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "CRITICAL":
                            setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #cc0000; -fx-font-weight: bold;");
                            break;
                        case "HIGH":
                            setStyle("-fx-background-color: #ffe6cc; -fx-text-fill: #cc6600; -fx-font-weight: bold;");
                            break;
                        case "MEDIUM":
                            setStyle("-fx-background-color: #ffffcc; -fx-text-fill: #996600;");
                            break;
                        case "LOW":
                            setStyle("-fx-background-color: #cce6ff; -fx-text-fill: #0066cc;");
                            break;
                    }
                }
            }
        });
        
        // Date formatting
        dateColumn.setCellFactory(column -> new TableCell<Alert, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        alertsTable.setItems(alertsList);
        
        // Context menu for alerts
        ContextMenu contextMenu = new ContextMenu();
        MenuItem markReadItem = new MenuItem("Mark as Read");
        MenuItem dismissItem = new MenuItem("Dismiss Alert");
        
        markReadItem.setOnAction(e -> markAlertAsRead());
        dismissItem.setOnAction(e -> dismissAlert());
        
        contextMenu.getItems().addAll(markReadItem, dismissItem);
        alertsTable.setContextMenu(contextMenu);
    }
    
    private void loadDashboardData() {
        try {
            // Load statistics
            long totalLifts = liftService.countActiveLifts();
            long activeLifts = liftService.countLiftsByStatus(Lift.LiftStatus.ACTIVE);
            long totalEmployees = employeeService.countActiveEmployees();
            
            LocalDate now = LocalDate.now();
            Double monthlyExpenses = expenseService.getMonthlyTotalExpenses(now.getYear(), now.getMonthValue());
            
            Platform.runLater(() -> {
                totalLiftsLabel.setText(String.valueOf(totalLifts));
                activeLiftsLabel.setText(String.valueOf(activeLifts));
                totalEmployeesLabel.setText(String.valueOf(totalEmployees));
                monthlyExpensesLabel.setText(String.format("₹ %.2f", monthlyExpenses != null ? monthlyExpenses : 0.0));
            });
            
            // Load alerts
            loadAlerts();
            
            log.info("Dashboard data loaded successfully");
            
        } catch (Exception e) {
            log.error("Error loading dashboard data", e);
            showError("Error loading dashboard data: " + e.getMessage());
        }
    }
    
    private void loadAlerts() {
        try {
            List<Alert> alerts = alertService.findUnreadAlerts();
            alertsList.clear();
            alertsList.addAll(alerts);
            
            log.info("Loaded {} alerts", alerts.size());
        } catch (Exception e) {
            log.error("Error loading alerts", e);
        }
    }
    
    private void setupEventHandlers() {
        refreshButton.setOnAction(e -> refreshDashboard());
        manageLiftsButton.setOnAction(e -> openLiftManagement());
        manageEmployeesButton.setOnAction(e -> openEmployeeManagement());
        serviceRecordsButton.setOnAction(e -> openServiceRecords());
        expenseRecordsButton.setOnAction(e -> openExpenseRecords());
        attendanceButton.setOnAction(e -> openAttendanceManagement());
        reportsButton.setOnAction(e -> openReports());
    }
    
    @FXML
    private void refreshDashboard() {
        log.info("Refreshing dashboard...");
        reminderService.runManualCheck();
        loadDashboardData();
    }
    
    private void markAlertAsRead() {
        Alert selectedAlert = alertsTable.getSelectionModel().getSelectedItem();
        if (selectedAlert != null) {
            alertService.markAsRead(selectedAlert.getId());
            loadAlerts();
        }
    }
    
    private void dismissAlert() {
        Alert selectedAlert = alertsTable.getSelectionModel().getSelectedItem();
        if (selectedAlert != null) {
            alertService.dismissAlert(selectedAlert.getId(), "Dismissed by user");
            loadAlerts();
        }
    }
    
    private void openLiftManagement() {
        try {
            Parent root = fxmlLoader.load("/fxml/lift_input.fxml");
            Stage stage = new Stage();
            stage.setTitle("Lift Management");
            stage.setScene(new Scene(root, 900, 700));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Refresh dashboard after closing
            loadDashboardData();
        } catch (IOException e) {
            log.error("Error opening lift management", e);
            showError("Could not open Lift Management window");
        }
    }
    
    private void openEmployeeManagement() {
        showInfo("Employee Management", "Employee management screen coming soon!");
    }
    
    private void openServiceRecords() {
        showInfo("Service Records", "Service records screen coming soon!");
    }
    
    private void openExpenseRecords() {
        showInfo("Expense Records", "Expense records screen coming soon!");
    }
    
    private void openAttendanceManagement() {
        showInfo("Attendance", "Attendance management screen coming soon!");
    }
    
    private void openReports() {
        showInfo("Reports", "Reports generation screen coming soon!");
    }
    
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
