# Quick Setup Guide

## Prerequisites
- Java JDK 8 or higher
- Oracle Database (or modify for other databases)
- Apache Ant (for building)

## Quick Start

### 1. Database Setup
```sql
-- Connect to your Oracle database and run:

CREATE TABLE Student (
    ID NUMBER PRIMARY KEY,
    USERNAME VARCHAR2(50) NOT NULL,
    PASSWORD VARCHAR2(50) NOT NULL,
    MARKS NUMBER DEFAULT 0
);

CREATE TABLE Question (
    ID NUMBER PRIMARY KEY,
    QUESTION_TEXT VARCHAR2(500) NOT NULL,
    OPTION1 VARCHAR2(200) NOT NULL,
    OPTION2 VARCHAR2(200) NOT NULL,
    OPTION3 VARCHAR2(200) NOT NULL,
    OPTION4 VARCHAR2(200) NOT NULL,
    CORRECT_ANSWER VARCHAR2(1) NOT NULL
);

-- Insert test data
INSERT INTO Student VALUES (1, 'test', 'test', 0);
INSERT INTO Question VALUES (1, 'What is Java?', 'Language', 'Platform', 'Both', 'None', 'c');
```

### 2. Configure Database Connection
Edit `server/src/server/database.properties`:
```properties
db.driver=oracle.jdbc.driver.OracleDriver
db.url=jdbc:oracle:thin:@localhost:1521:xe
db.username=YOUR_DB_USERNAME
db.password=YOUR_DB_PASSWORD
server.port=5555
```

### 3. Build and Run

#### Terminal 1 - Start Server:
```bash
cd server
ant clean compile
ant run
```

#### Terminal 2 - Start Client:
```bash
cd client
ant clean compile
ant run
```

### 4. Test the System
- Login with username: `test`, password: `test`
- Answer the question
- View your results

## Build Commands

### Server
```bash
cd server
ant clean        # Clean previous builds
ant compile      # Compile source code
ant run          # Run the server
ant jar          # Create JAR file
```

### Client
```bash
cd client
ant clean        # Clean previous builds
ant compile      # Compile source code
ant run          # Run the client
ant jar          # Create JAR file
```

## Troubleshooting

### Server won't start
- Check if port 5555 is available
- Verify database connection settings
- Ensure Oracle JDBC driver is in classpath

### Client can't connect
- Ensure server is running first
- Check firewall settings
- Verify server IP address (default: localhost)

### Authentication fails
- Verify credentials exist in Student table
- Check database connection
- Review server logs for errors

## Configuration Files

- `server/src/server/database.properties` - Database settings
- `server/nbproject/project.properties` - Build configuration
- `client/nbproject/project.properties` - Build configuration

## Default Settings

- Server Port: 5555
- Database: Oracle (localhost:1521:xe)
- Question Limit: 10 per exam
- Default credentials: test/test