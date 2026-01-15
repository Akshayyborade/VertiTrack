# VertiTrack - Lift Maintenance & Expense Management System

A comprehensive JavaFX desktop application for managing lift maintenance records, AMC contracts, service schedules, expenses, and employee attendance with automated alert reminders.

## 📋 Table of Contents
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Requirements](#system-requirements)
- [Installation & Setup](#installation--setup)
- [Database Configuration](#database-configuration)
- [Project Structure](#project-structure)
- [Key Functionalities](#key-functionalities)
- [Usage Guide](#usage-guide)
- [Alert System](#alert-system)
- [Reports & Export](#reports--export)

---

## ✨ Features

### 1. **Lift Records Management**
- Complete lift information tracking (location, type, capacity, manufacturer)
- AMC (Annual Maintenance Contract) details management
- Quarterly payment scheduling and tracking
- Contractor information management
- Lift status monitoring (Active, Inactive, Under Maintenance, Decommissioned)

### 2. **Automated Alert & Reminder System** 🔔
- **AMC Renewal & Expiry Alerts**
  - Alerts at 30, 15, 7, and 3 days before expiry
  - Expired AMC notifications
  - Priority-based color coding (Critical/High/Medium/Low)

- **Quarterly Payment Reminders**
  - Alerts 15, 7, 3 days before payment due
  - Support for all 4 quarterly payments
  - Payment amount tracking

- **Service Due Reminders**
  - Overdue service notifications
  - Upcoming service schedules (15 days advance)

- **Employee Absence Tracking**
  - Daily absence alerts
  - Monthly attendance summaries

### 3. **AMC Service Records**
- **Individual Lift Service History**
  - AMC Servicing records (date-wise)
  - AMC Repairing records (date-wise)
  - Complete year-wise tracking
  - Service type categorization
  - Work description and parts tracking

- **Service Types Supported:**
  - AMC Servicing
  - AMC Repair
  - Breakdown Repair
  - Preventive Maintenance
  - Quarterly Service
  - Annual Inspection
  - Parts Replacement

### 4. **Payment & Expense Tracking** 💰
- **AMC & Repairing Payment Records**
  - Year-wise expense tracking
  - Payment status monitoring (Paid/Pending/Overdue)
  - Invoice management

- **Material Expense Records**
  - AMC-related material expenses
  - Parts purchase tracking
  - Cost categorization

- **Employee Expense Records**
  - Petrol expenses
  - Other expenses
  - Monthly expense summaries
  - Employee-wise expense tracking

### 5. **Employee Management** 👥
- Complete employee information
- Designation and department tracking
- Contact and emergency contact details
- Employee status management
- Auto-generated employee codes

### 6. **Attendance Management** 📋
- **Daily Attendance Marking**
  - Present/Absent/Half Day/Leave
  - Holiday and Week-off tracking
  - Work from Home support

- **Monthly Attendance Records**
  - Absence and presence tracking
  - Leave management
  - Work hours tracking
  - Overtime calculation

### 7. **Reports & Export**
- Yearly expense reports
- Service history reports
- Monthly attendance reports
- Employee expense reports
- CSV export functionality

---

## 🛠 Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Backend Framework** | Spring Boot | 3.2.3 |
| **UI Framework** | JavaFX | 21 |
| **Database** | MySQL | Latest |
| **ORM** | Hibernate/JPA | 6.4.4 |
| **Build Tool** | Maven | Latest |
| **Java** | JDK | 17 |
| **Scheduling** | Spring @Scheduled | - |
| **Data Binding** | Lombok | Latest |

---

## 💻 System Requirements

- **Java Development Kit (JDK)**: 17 or higher
- **MySQL Server**: 8.0 or higher
- **Maven**: 3.6 or higher
- **Operating System**: Windows 10/11, macOS, Linux
- **RAM**: Minimum 4GB (8GB recommended)
- **Disk Space**: Minimum 500MB

---

## 🚀 Installation & Setup

### 1. **Clone the Repository**
```bash
git clone <repository-url>
cd VertiTrack
```

### 2. **Install MySQL**
- Download and install MySQL Server from [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/)
- Create a MySQL user with appropriate privileges

### 3. **Configure Database**
Edit `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/vertitrack?createDatabaseIfNotExist=true&useSSL=false
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Note:** Replace `YOUR_USERNAME` and `YOUR_PASSWORD` with your MySQL credentials.

### 4. **Build the Project**
```bash
mvn clean install
```

### 5. **Run the Application**
```bash
mvn spring-boot:run
```

Or run the main class directly:
```bash
java -jar target/vertitrack-app-1.0.0-SNAPSHOT.jar
```

---

## 🗄 Database Configuration

The application uses MySQL database. On first run, Hibernate will automatically create the following tables:

- `lifts` - Lift master data
- `service_records` - Service history
- `expenses` - All expense records
- `employees` - Employee master data
- `attendance` - Daily attendance records
- `alerts` - Alert notifications

**Initial Setup:**
1. Ensure MySQL service is running
2. The database `vertitrack` will be created automatically
3. Tables will be created on first application startup
4. Sample data can be added through the UI

---

## 📁 Project Structure

```
VertiTrack/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/vertitrack/
│   │   │       ├── config/
│   │   │       │   ├── SpringFXMLLoader.java
│   │   │       │   └── StageInitializer.java
│   │   │       ├── controller/
│   │   │       │   ├── DashboardController.java
│   │   │       │   ├── EmployeeController.java
│   │   │       │   └── LiftController.java
│   │   │       ├── event/
│   │   │       │   └── StageReadyEvent.java
│   │   │       ├── model/
│   │   │       │   ├── Alert.java
│   │   │       │   ├── Attendance.java
│   │   │       │   ├── Employee.java
│   │   │       │   ├── Expense.java
│   │   │       │   ├── Lift.java
│   │   │       │   └── ServiceRecord.java
│   │   │       ├── repository/
│   │   │       │   ├── AlertRepository.java
│   │   │       │   ├── AttendanceRepository.java
│   │   │       │   ├── EmployeeRepository.java
│   │   │       │   ├── ExpenseRepository.java
│   │   │       │   ├── LiftRepository.java
│   │   │       │   └── ServiceRecordRepository.java
│   │   │       ├── service/
│   │   │       │   ├── AlertService.java
│   │   │       │   ├── AttendanceService.java
│   │   │       │   ├── EmployeeService.java
│   │   │       │   ├── ExpenseService.java
│   │   │       │   ├── ExportService.java
│   │   │       │   ├── LiftService.java
│   │   │       │   ├── ReminderService.java
│   │   │       │   └── ServiceRecordService.java
│   │   │       ├── JavaFxMain.java
│   │   │       └── VertiTrackApp.java
│   │   └── resources/
│   │       ├── css/
│   │       │   └── styles.css
│   │       ├── fxml/
│   │       │   ├── dashboard.fxml
│   │       │   └── lift_input.fxml
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

---

## 🎯 Key Functionalities

### Dashboard
- Real-time statistics (Total Lifts, Active Lifts, Employees, Monthly Expenses)
- Alert panel with priority-based color coding
- Quick navigation to all modules
- Manual refresh for alerts

### Lift Management
- Add/Edit/Delete lift records
- Search and filter lifts
- Complete AMC details entry
- Quarterly payment date scheduling
- Contractor information management

### Service Records
- Individual lift service history
- Year-wise service tracking
- AMC servicing vs. repairing separation
- Cost tracking (Labor + Parts)
- Invoice management

### Expense Management
- Comprehensive expense categorization
- Lift-specific expenses
- Employee-specific expenses
- Payment status tracking
- Monthly and yearly summaries

### Employee Management
- Employee master data
- Auto-generated employee codes
- Department and designation tracking
- Status management

### Attendance
- Daily attendance marking
- Monthly attendance summaries
- Present/Absent/Leave tracking
- Work hours and overtime calculation

---

## 🔔 Alert System

The automated alert system runs daily at **9:00 AM** and checks for:

### AMC Expiry Alerts
- **30 days before**: Medium priority
- **15 days before**: High priority
- **7 days before**: Critical priority
- **3 days or less**: Critical priority
- **Expired**: Critical priority

### Quarterly Payment Alerts
- **15 days before**: Medium priority
- **7 days before**: High priority
- **3 days before**: Critical priority
- **Due date**: Critical priority

### Service Due Alerts
- Checks for overdue services
- Alerts for services due in next 15 days

### Manual Alert Check
- Click "Refresh Alerts" button on dashboard
- Runs complete alert check immediately

### Alert Actions
- **Mark as Read**: Mark notification as read
- **Dismiss**: Remove alert from active list
- **Right-click** on alert for context menu

---

## 📊 Reports & Export

### Export Functionality
All data can be exported to CSV format for Excel:

1. **Lift Records Export**
   - All lift details
   - AMC information
   - Contractor details

2. **Service Records Export**
   - Year-wise service history
   - AMC servicing records
   - AMC repair records
   - Total cost summary

3. **Expense Reports**
   - Yearly expense breakdown
   - Category-wise expenses
   - Payment status tracking
   - Total summaries

4. **Attendance Reports**
   - Monthly attendance records
   - Attendance summary
   - Work hours tracking
   - Employee-wise reports

5. **Employee Expense Reports**
   - Monthly expense tracking
   - Petrol and other expenses
   - Total expense calculations

### How to Export
```java
// Example: Export yearly expenses
exportService.exportYearlyExpenseReportToCSV(2024, "C:/exports/expenses_2024.csv");

// Example: Export lift service records
exportService.exportLiftServiceRecordsToCSV(liftId, 2024, "C:/exports/lift_services.csv");
```

---

## 📝 Usage Guide

### Adding a New Lift
1. Click **"Manage Lifts"** from dashboard
2. Fill in all required fields (marked with *)
3. Set AMC dates and quarterly payment dates
4. Enter contractor details
5. Click **"Save Lift"**

### Recording Service Work
1. Navigate to **"Service Records"**
2. Select the lift
3. Choose service type (AMC Servicing/Repair)
4. Enter service details and costs
5. Set next service date
6. Save the record

### Adding Expenses
1. Go to **"Expenses"** module
2. Select expense type
3. Link to lift or employee if applicable
4. Enter amount and payment details
5. Upload invoice (optional)
6. Save expense record

### Marking Attendance
1. Navigate to **"Attendance"**
2. Select employee
3. Choose date and status
4. Enter work hours if applicable
5. Add remarks if needed
6. Save attendance record

### Viewing Alerts
1. Dashboard shows all active alerts
2. Color-coded by priority (Red/Orange/Yellow/Blue)
3. Right-click to mark as read or dismiss
4. Click "Refresh Alerts" to check for new alerts

---

## 🔧 Customization

### Changing Alert Schedule
Edit `ReminderService.java`:
```java
@Scheduled(cron = "0 0 9 * * *") // Runs daily at 9:00 AM
public void checkDailyReminders() {
    // Alert checking logic
}
```

### Modifying Alert Thresholds
Edit alert creation methods in `ReminderService.java` to change when alerts are triggered.

### Custom Export Formats
Extend `ExportService.java` to add PDF or Excel export functionality.

---

## 🐛 Troubleshooting

### Application Won't Start
- Check if MySQL is running
- Verify database credentials in `application.properties`
- Ensure JDK 17 is installed
- Check if port 3306 is available

### Alerts Not Appearing
- Check if `@EnableScheduling` is present in `VertiTrackApp.java`
- Verify system time is correct
- Check database for alert records
- Click "Refresh Alerts" manually

### Database Connection Issues
- Verify MySQL service is running
- Check firewall settings
- Ensure database user has proper privileges
- Test connection using MySQL Workbench

---

## 📄 License

This project is proprietary software. All rights reserved.

---

## 👥 Support

For support, please contact the development team or raise an issue in the project repository.

---

## 🎉 Acknowledgments

Built with:
- Spring Boot
- JavaFX
- MySQL
- Hibernate
- Lombok

---

**VertiTrack** - Making lift maintenance management simple and efficient! 🏢✨
