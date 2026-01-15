package com.vertitrack.controller;

import com.vertitrack.model.Lift;
import com.vertitrack.service.LiftService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LiftController {
    
    private final LiftService liftService;
    
    // Form Fields
    @FXML private TextField liftNumberField;
    @FXML private TextField locationField;
    @FXML private TextField buildingField;
    @FXML private TextField liftTypeField;
    @FXML private TextField capacityField;
    @FXML private TextField floorsField;
    @FXML private TextField manufacturerField;
    @FXML private TextField modelField;
    
    // AMC Fields
    @FXML private DatePicker amcStartDatePicker;
    @FXML private DatePicker amcEndDatePicker;
    @FXML private DatePicker amcRenewalDatePicker;
    @FXML private TextField amcAmountField;
    
    // Quarterly Payment Fields
    @FXML private DatePicker quarter1DatePicker;
    @FXML private DatePicker quarter2DatePicker;
    @FXML private DatePicker quarter3DatePicker;
    @FXML private DatePicker quarter4DatePicker;
    @FXML private TextField quarterlyAmountField;
    
    // Contractor Fields
    @FXML private TextField contractorNameField;
    @FXML private TextField contractorContactField;
    @FXML private TextField contractorEmailField;
    
    // Status and Notes
    @FXML private ComboBox<Lift.LiftStatus> statusComboBox;
    @FXML private TextArea notesArea;
    
    // Buttons
    @FXML private Button saveButton;
    @FXML private Button clearButton;
    @FXML private Button deleteButton;
    
    // Table
    @FXML private TableView<Lift> liftsTable;
    @FXML private TableColumn<Lift, String> liftNumberColumn;
    @FXML private TableColumn<Lift, String> locationColumn;
    @FXML private TableColumn<Lift, String> buildingColumn;
    @FXML private TableColumn<Lift, LocalDate> amcEndDateColumn;
    @FXML private TableColumn<Lift, String> statusColumn;
    
    @FXML private TextField searchField;
    
    private ObservableList<Lift> liftsList = FXCollections.observableArrayList();
    private Lift selectedLift = null;
    
    @FXML
    public void initialize() {
        log.info("Initializing Lift Controller");
        
        setupStatusComboBox();
        setupTable();
        loadAllLifts();
        setupEventHandlers();
    }
    
    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(Lift.LiftStatus.values()));
        statusComboBox.setValue(Lift.LiftStatus.ACTIVE);
    }
    
    private void setupTable() {
        liftNumberColumn.setCellValueFactory(new PropertyValueFactory<>("liftNumber"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        buildingColumn.setCellValueFactory(new PropertyValueFactory<>("building"));
        amcEndDateColumn.setCellValueFactory(new PropertyValueFactory<>("amcEndDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        liftsTable.setItems(liftsList);
        
        // Row selection handler
        liftsTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadLiftToForm(newValue);
                }
            }
        );
    }
    
    private void loadAllLifts() {
        try {
            List<Lift> lifts = liftService.findAllLifts();
            liftsList.clear();
            liftsList.addAll(lifts);
            log.info("Loaded {} lifts", lifts.size());
        } catch (Exception e) {
            log.error("Error loading lifts", e);
            showError("Error loading lifts: " + e.getMessage());
        }
    }
    
    private void setupEventHandlers() {
        saveButton.setOnAction(e -> saveLift());
        clearButton.setOnAction(e -> clearForm());
        deleteButton.setOnAction(e -> deleteLift());
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> searchLifts(newValue));
    }
    
    @FXML
    private void saveLift() {
        try {
            // Validation
            if (!validateForm()) {
                return;
            }
            
            Lift lift;
            if (selectedLift != null) {
                lift = selectedLift;
            } else {
                lift = new Lift();
            }
            
            // Set basic fields
            lift.setLiftNumber(liftNumberField.getText().trim());
            lift.setLocation(locationField.getText().trim());
            lift.setBuilding(buildingField.getText().trim());
            lift.setLiftType(liftTypeField.getText().trim());
            lift.setManufacturer(manufacturerField.getText().trim());
            lift.setModel(modelField.getText().trim());
            
            // Set numeric fields
            if (!capacityField.getText().isEmpty()) {
                lift.setCapacity(Integer.parseInt(capacityField.getText()));
            }
            if (!floorsField.getText().isEmpty()) {
                lift.setFloors(Integer.parseInt(floorsField.getText()));
            }
            
            // AMC fields
            lift.setAmcStartDate(amcStartDatePicker.getValue());
            lift.setAmcEndDate(amcEndDatePicker.getValue());
            lift.setAmcRenewalDate(amcRenewalDatePicker.getValue());
            
            if (!amcAmountField.getText().isEmpty()) {
                lift.setAmcAmount(Double.parseDouble(amcAmountField.getText()));
            }
            
            // Quarterly dates
            lift.setQuarter1PaymentDate(quarter1DatePicker.getValue());
            lift.setQuarter2PaymentDate(quarter2DatePicker.getValue());
            lift.setQuarter3PaymentDate(quarter3DatePicker.getValue());
            lift.setQuarter4PaymentDate(quarter4DatePicker.getValue());
            
            if (!quarterlyAmountField.getText().isEmpty()) {
                lift.setQuarterlyAmount(Double.parseDouble(quarterlyAmountField.getText()));
            }
            
            // Contractor fields
            lift.setContractorName(contractorNameField.getText().trim());
            lift.setContractorContact(contractorContactField.getText().trim());
            lift.setContractorEmail(contractorEmailField.getText().trim());
            
            // Status and notes
            lift.setStatus(statusComboBox.getValue());
            lift.setNotes(notesArea.getText().trim());
            
            // Save
            liftService.saveLift(lift);
            
            showInfo("Success", "Lift saved successfully!");
            clearForm();
            loadAllLifts();
            
        } catch (NumberFormatException e) {
            showError("Invalid number format. Please check capacity, floors, and amount fields.");
        } catch (Exception e) {
            log.error("Error saving lift", e);
            showError("Error saving lift: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        if (liftNumberField.getText().trim().isEmpty()) {
            showError("Lift Number is required!");
            return false;
        }
        if (locationField.getText().trim().isEmpty()) {
            showError("Location is required!");
            return false;
        }
        if (amcStartDatePicker.getValue() == null) {
            showError("AMC Start Date is required!");
            return false;
        }
        if (amcEndDatePicker.getValue() == null) {
            showError("AMC End Date is required!");
            return false;
        }
        if (amcRenewalDatePicker.getValue() == null) {
            showError("AMC Renewal Date is required!");
            return false;
        }
        return true;
    }
    
    @FXML
    private void clearForm() {
        selectedLift = null;
        
        liftNumberField.clear();
        locationField.clear();
        buildingField.clear();
        liftTypeField.clear();
        capacityField.clear();
        floorsField.clear();
        manufacturerField.clear();
        modelField.clear();
        
        amcStartDatePicker.setValue(null);
        amcEndDatePicker.setValue(null);
        amcRenewalDatePicker.setValue(null);
        amcAmountField.clear();
        
        quarter1DatePicker.setValue(null);
        quarter2DatePicker.setValue(null);
        quarter3DatePicker.setValue(null);
        quarter4DatePicker.setValue(null);
        quarterlyAmountField.clear();
        
        contractorNameField.clear();
        contractorContactField.clear();
        contractorEmailField.clear();
        
        statusComboBox.setValue(Lift.LiftStatus.ACTIVE);
        notesArea.clear();
        
        liftsTable.getSelectionModel().clearSelection();
    }
    
    private void loadLiftToForm(Lift lift) {
        selectedLift = lift;
        
        liftNumberField.setText(lift.getLiftNumber());
        locationField.setText(lift.getLocation());
        buildingField.setText(lift.getBuilding());
        liftTypeField.setText(lift.getLiftType());
        manufacturerField.setText(lift.getManufacturer());
        modelField.setText(lift.getModel());
        
        capacityField.setText(lift.getCapacity() != null ? lift.getCapacity().toString() : "");
        floorsField.setText(lift.getFloors() != null ? lift.getFloors().toString() : "");
        
        amcStartDatePicker.setValue(lift.getAmcStartDate());
        amcEndDatePicker.setValue(lift.getAmcEndDate());
        amcRenewalDatePicker.setValue(lift.getAmcRenewalDate());
        amcAmountField.setText(lift.getAmcAmount() != null ? lift.getAmcAmount().toString() : "");
        
        quarter1DatePicker.setValue(lift.getQuarter1PaymentDate());
        quarter2DatePicker.setValue(lift.getQuarter2PaymentDate());
        quarter3DatePicker.setValue(lift.getQuarter3PaymentDate());
        quarter4DatePicker.setValue(lift.getQuarter4PaymentDate());
        quarterlyAmountField.setText(lift.getQuarterlyAmount() != null ? lift.getQuarterlyAmount().toString() : "");
        
        contractorNameField.setText(lift.getContractorName());
        contractorContactField.setText(lift.getContractorContact());
        contractorEmailField.setText(lift.getContractorEmail());
        
        statusComboBox.setValue(lift.getStatus());
        notesArea.setText(lift.getNotes());
    }
    
    @FXML
    private void deleteLift() {
        if (selectedLift == null) {
            showError("Please select a lift to delete!");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Lift: " + selectedLift.getLiftNumber());
        confirmAlert.setContentText("Are you sure you want to delete this lift?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                liftService.deleteLift(selectedLift.getId());
                showInfo("Success", "Lift deleted successfully!");
                clearForm();
                loadAllLifts();
            } catch (Exception e) {
                log.error("Error deleting lift", e);
                showError("Error deleting lift: " + e.getMessage());
            }
        }
    }
    
    private void searchLifts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadAllLifts();
            return;
        }
        
        try {
            List<Lift> results = liftService.searchLifts(keyword.trim());
            liftsList.clear();
            liftsList.addAll(results);
        } catch (Exception e) {
            log.error("Error searching lifts", e);
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
