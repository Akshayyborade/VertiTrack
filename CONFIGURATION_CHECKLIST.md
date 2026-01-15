# VertiTrack - Configuration Checklist ✓

Before running the application, please complete this checklist:

## ☑️ Prerequisites

- [ ] **Java JDK 17 or higher** installed
  ```bash
  java -version
  # Should show: java version "17.x.x" or higher
  ```

- [ ] **Maven 3.6+** installed
  ```bash
  mvn -version
  # Should show: Apache Maven 3.x.x
  ```

- [ ] **MySQL 8.0+** installed and running
  ```bash
  mysql --version
  # Should show: mysql Ver 8.x.x
  ```

---

## ☑️ Database Configuration

### Step 1: MySQL Service Running
- [ ] MySQL service is started
  - **Windows**: Check Services → MySQL80
  - **Mac**: `brew services list | grep mysql`
  - **Linux**: `sudo systemctl status mysql`

### Step 2: Create Database User (Recommended)
- [ ] Login to MySQL as root:
  ```sql
  mysql -u root -p
  ```

- [ ] Create dedicated user:
  ```sql
  CREATE USER 'vertitrack_user'@'localhost' IDENTIFIED BY 'VertiTrack@123';
  ```

- [ ] Grant privileges:
  ```sql
  GRANT ALL PRIVILEGES ON vertitrack.* TO 'vertitrack_user'@'localhost';
  FLUSH PRIVILEGES;
  EXIT;
  ```

### Step 3: Update Application Properties
- [ ] Open: `src/main/resources/application.properties`
- [ ] Update these lines:
  ```properties
  spring.datasource.username=vertitrack_user
  spring.datasource.password=VertiTrack@123
  ```
  *(Replace with your actual MySQL username and password)*

---

## ☑️ Build Configuration

- [ ] Navigate to project directory:
  ```bash
  cd D:\VertiTrack
  ```

- [ ] Clean previous builds:
  ```bash
  mvn clean
  ```

- [ ] Install dependencies:
  ```bash
  mvn install
  ```
  *(This may take 2-3 minutes on first run)*

---

## ☑️ First Run Checklist

- [ ] All Maven dependencies downloaded successfully
- [ ] No compilation errors
- [ ] Database `vertitrack` created (auto-created on first run)
- [ ] All 6 tables created automatically:
  - lifts
  - service_records
  - expenses
  - employees
  - attendance
  - alerts

---

## ☑️ Verify Installation

### Test Database Connection
```bash
mysql -u vertitrack_user -p
# Enter password: VertiTrack@123

USE vertitrack;
SHOW TABLES;
# Should show 6 tables after first run
```

### Test Application Startup
```bash
mvn spring-boot:run
```

**Expected Output:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.3)

...
VertiTrack application started successfully
```

The JavaFX window should open automatically showing the dashboard.

---

## ☑️ Post-Installation Verification

### 1. Dashboard Loads
- [ ] Main window appears
- [ ] Statistics cards visible (all showing 0 initially)
- [ ] Alert table visible (empty initially)
- [ ] Navigation buttons visible

### 2. Lift Management Works
- [ ] Click "Manage Lifts" button
- [ ] Lift input form appears
- [ ] Can enter data in all fields
- [ ] Date pickers work
- [ ] Save button functional

### 3. Database Tables Created
```sql
-- Run in MySQL:
SELECT COUNT(*) FROM information_schema.tables 
WHERE table_schema = 'vertitrack';
-- Should return: 6
```

---

## ☑️ Optional Configuration

### Change Alert Schedule
Edit `src/main/java/com/vertitrack/service/ReminderService.java`:
```java
@Scheduled(cron = "0 0 9 * * *") // Default: 9:00 AM daily
// Change to your preferred time
// Format: second minute hour day month weekday
```

### Modify Alert Thresholds
Edit `ReminderService.java` alert creation methods to customize when alerts appear.

### Database Timezone
If timezone issues occur, add to `application.properties`:
```properties
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
```

---

## ☑️ Common Issues & Solutions

### Issue: "Can't connect to MySQL server"
**Solution:**
1. Verify MySQL is running
2. Check username/password in `application.properties`
3. Ensure MySQL is on port 3306
4. Check firewall settings

### Issue: "Port 3306 already in use"
**Solution:**
Check if another MySQL instance is running or change port in `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/vertitrack?createDatabaseIfNotExist=true&useSSL=false
```

### Issue: JavaFX components not found
**Solution:**
Ensure Maven downloaded JavaFX properly:
```bash
mvn dependency:tree | grep javafx
```

### Issue: "Table doesn't exist"
**Solution:**
Drop and recreate database:
```sql
DROP DATABASE vertitrack;
CREATE DATABASE vertitrack;
```
Then restart application.

---

## ☑️ Performance Optimization (Optional)

### MySQL Configuration
Add to MySQL `my.ini` or `my.cnf`:
```ini
[mysqld]
max_connections=50
innodb_buffer_pool_size=256M
```

### Java Memory Settings
If large dataset, increase heap size:
```bash
export MAVEN_OPTS="-Xmx1024m"
mvn spring-boot:run
```

---

## ☑️ Security Checklist

- [ ] Change default database password
- [ ] Don't commit `application.properties` with credentials to Git
- [ ] Use environment variables for production:
  ```properties
  spring.datasource.username=${DB_USERNAME}
  spring.datasource.password=${DB_PASSWORD}
  ```

---

## ☑️ Backup Strategy

### Daily Backup Script (Recommended)
**Windows (backup.bat):**
```batch
mysqldump -u vertitrack_user -pVertiTrack@123 vertitrack > backup_%date:~-4,4%%date:~-10,2%%date:~-7,2%.sql
```

**Linux/Mac (backup.sh):**
```bash
mysqldump -u vertitrack_user -pVertiTrack@123 vertitrack > backup_$(date +%Y%m%d).sql
```

Schedule this to run daily.

---

## ☑️ Ready to Go!

Once all checkboxes are ticked:

1. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Add your first lift** (Manage Lifts)

3. **Add employees** (when Employee Management is implemented)

4. **Start tracking!**

---

## 📞 Need Help?

If you encounter issues not covered here:

1. Check `README.md` for detailed documentation
2. Review `QUICKSTART.md` for usage examples
3. Check application logs in console
4. Verify database connection manually
5. Contact support team

---

## ✅ Configuration Complete!

**Status Indicators:**
- 🟢 All configured correctly → Ready to use
- 🟡 Minor warnings → Application will work, optimize later
- 🔴 Errors present → Review checklist again

---

**Remember:** First run may take longer as Hibernate creates tables. Subsequent runs will be faster!

Good luck with VertiTrack! 🚀
