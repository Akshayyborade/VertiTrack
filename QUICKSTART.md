# VertiTrack - Quick Start Guide

Get up and running with VertiTrack in 5 minutes!

## ⚡ Quick Setup

### Step 1: Prerequisites Check
```bash
# Check Java version (should be 17+)
java -version

# Check Maven version
mvn -version

# Check MySQL status
mysql --version
```

### Step 2: Database Setup
```sql
-- Login to MySQL
mysql -u root -p

-- Create database (or let Spring Boot create it automatically)
CREATE DATABASE vertitrack;

-- Create user (recommended)
CREATE USER 'vertitrack_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON vertitrack.* TO 'vertitrack_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### Step 3: Configure Application
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=vertitrack_user
spring.datasource.password=your_password
```

### Step 4: Build & Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

**That's it!** The application will open automatically.

---

## 📚 First-Time Usage

### 1. Add Your First Lift
1. Click **"Manage Lifts"** button
2. Fill in the form:
   - **Lift Number**: LIFT-001
   - **Location**: Main Building, Ground Floor
   - **AMC Start Date**: Select date
   - **AMC End Date**: One year from start
   - **AMC Renewal Date**: Before end date
   - **Quarterly Dates**: Every 3 months
3. Click **"Save Lift"**

### 2. Set Up Quarterly Payments
When adding a lift, set these dates:
- **Q1**: 3 months after AMC start
- **Q2**: 6 months after AMC start
- **Q3**: 9 months after AMC start
- **Q4**: 12 months after AMC start

### 3. View Dashboard
- Return to dashboard (close Lift Management window)
- Click **"Refresh Alerts"**
- You'll see alerts for upcoming AMC renewals and payments

### 4. Add Service Records
1. Click **"Service Records"**
2. Select a lift
3. Add service details
4. Save the record

### 5. Track Expenses
1. Click **"Expenses"**
2. Select expense type
3. Enter amount and details
4. Link to lift if applicable
5. Save expense

---

## 🎯 Sample Data Entry

### Example Lift Entry
```
Lift Number: LIFT-001
Location: Main Office Building, 5th Floor
Building: Corporate HQ
Type: Passenger
Capacity: 8 persons / 630 kg
Floors: 10
Manufacturer: Otis
Model: Gen2-MRL

AMC Start: 01-01-2024
AMC End: 31-12-2024
AMC Renewal: 15-12-2024
AMC Amount: ₹50,000

Quarter 1: 01-04-2024 (₹12,500)
Quarter 2: 01-07-2024 (₹12,500)
Quarter 3: 01-10-2024 (₹12,500)
Quarter 4: 01-01-2025 (₹12,500)

Contractor: ABC Lifts Pvt Ltd
Contact: +91-9876543210
Email: service@abclifts.com
```

### Example Employee Entry
```
Employee Code: EMP001 (auto-generated)
First Name: Rajesh
Last Name: Kumar
Designation: Technician
Department: Maintenance
Contact: +91-9876543210
Email: rajesh@company.com
Joining Date: 01-01-2024
Salary: ₹30,000
Status: Active
```

### Example Service Record
```
Lift: LIFT-001
Service Type: AMC Servicing
Service Date: 15-01-2024
Performed By: Rajesh Kumar
Work Description: Regular maintenance check, lubrication
Parts Replaced: Cable pulley
Labor Cost: ₹2,000
Parts Cost: ₹1,500
Total: ₹3,500
Status: Completed
Next Service Date: 15-02-2024
```

### Example Expense Entry
```
Type: AMC Payment
Category: Quarterly Payment - Q1
Amount: ₹12,500
Date: 01-04-2024
Lift: LIFT-001
Paid To: ABC Lifts Pvt Ltd
Payment Mode: Bank Transfer
Transaction Ref: TXN123456789
Invoice: INV-Q1-2024
Status: Paid
```

---

## 🔔 Understanding Alerts

### Alert Priority Colors
- 🔴 **RED (Critical)**: 0-7 days or overdue - Immediate action required
- 🟠 **ORANGE (High)**: 8-15 days - Urgent attention needed
- 🟡 **YELLOW (Medium)**: 16-30 days - Plan ahead
- 🔵 **BLUE (Low)**: Information only

### Common Alerts You'll See

1. **AMC Expiring Soon**
   - Appears 30 days before expiry
   - Gets more critical as date approaches

2. **Quarterly Payment Due**
   - Alerts 15 days before payment date
   - Helps you plan cash flow

3. **Service Overdue**
   - When next service date has passed
   - Ensure timely maintenance

---

## 💡 Tips & Best Practices

### 1. Regular Data Entry
- Add service records immediately after service
- Mark attendance daily
- Record expenses as they occur

### 2. Use Search Feature
- Quick search in all modules
- Search by lift number, location, employee name

### 3. Review Alerts Daily
- Check dashboard every morning
- Address critical alerts first
- Mark alerts as read after action

### 4. Export Reports Monthly
- Generate expense reports monthly
- Review service history quarterly
- Export attendance for payroll

### 5. Keep Data Updated
- Update contractor contacts regularly
- Adjust AMC dates when renewed
- Update employee status changes

---

## 🔧 Common Tasks

### How to Renew AMC
1. Open lift in Lift Management
2. Update AMC End Date to new date
3. Update AMC Renewal Date
4. Update quarterly payment dates
5. Save changes

### How to Record a Repair
1. Go to Service Records
2. Select lift
3. Choose "AMC Repair" as service type
4. Enter repair details and cost
5. Save record
6. Add expense entry for payment

### How to Track Monthly Employee Expenses
1. Go to Expenses
2. Filter by employee and month
3. View total expenses
4. Export report if needed

### How to Generate Reports
1. Click Reports button
2. Select report type
3. Choose date range
4. Export to CSV
5. Open in Excel for analysis

---

## ❓ FAQ

**Q: How often do alerts refresh?**
A: Automatically at 9:00 AM daily, or click "Refresh Alerts" manually.

**Q: Can I change alert timing?**
A: Yes, edit `ReminderService.java` and modify the `@Scheduled` annotation.

**Q: Where are exports saved?**
A: You specify the path when exporting. Default: User's Documents folder.

**Q: Can I delete old alerts?**
A: Yes, right-click and select "Dismiss". Old dismissed alerts auto-delete after 90 days.

**Q: How to backup data?**
A: Use MySQL backup tools or export all records to CSV regularly.

**Q: Can multiple users access simultaneously?**
A: Currently designed for single user. For multi-user, deploy on network.

---

## 🆘 Need Help?

### Check Logs
Application logs are in the console when running via Maven.

### Test Database Connection
```bash
mysql -u vertitrack_user -p vertitrack
SHOW TABLES;
```

### Reset Data
```sql
-- ⚠️ WARNING: This deletes all data!
DROP DATABASE vertitrack;
CREATE DATABASE vertitrack;
```

### Community Support
- Check README.md for detailed documentation
- Review code comments
- Contact support team

---

## 🎓 Next Steps

1. ✅ Complete first lift entry
2. ✅ Add employee records
3. ✅ Record first service
4. ✅ Add initial expenses
5. ✅ Set up attendance tracking
6. ✅ Review dashboard alerts daily

---

**Happy Tracking!** 🚀

Your lift maintenance management just got easier with VertiTrack!
