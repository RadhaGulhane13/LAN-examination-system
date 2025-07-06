# LAN Examination System

![Architecture Diagram!](docs/LAN_arch_diag.png)

## About LAN Examination System

The LAN Examination System is a secure, scalable, and efficient platform designed to conduct online examinations in a Local Area Network (LAN) environment, replacing the traditional pen-and-paper examination method.

This is a client-server based project where:
- **Server Module**: Acts as an examiner, responsible for handling examination activities including client authentication, question distribution, answer evaluation, and database management
- **Client Module**: Provides a graphical user interface (GUI) for students, offering a secure online examination environment where students can receive questions from the server and submit their responses

## Key Features

- ✅ **Secure Authentication**: User validation against database credentials
- ✅ **Real-time Examination**: Live question-answer exchange between client and server
- ✅ **Automatic Scoring**: Instant evaluation and marks calculation
- ✅ **Multi-client Support**: Concurrent examination sessions using multithreading
- ✅ **Database Integration**: Persistent storage of questions, answers, and results
- ✅ **User-friendly Interface**: Intuitive GUI for both login and examination screens
- ✅ **Configuration Management**: External configuration for database settings

## Technologies Used

- **Programming Language**: Java (JDK 8+)
- **GUI Framework**: Java Swing for user interface
- **Networking**: Socket Programming for client-server communication
- **Database**: Oracle Database with JDBC connectivity
- **Concurrency**: Multithreading for handling multiple clients simultaneously
- **Build Tool**: Apache Ant for compilation and building
- **Architecture**: Multi-tier client-server architecture

## System Requirements

### Server Requirements
- Java Runtime Environment (JRE) 8 or higher
- Oracle Database (11g or higher)
- Oracle JDBC Driver
- Minimum 512 MB RAM
- Network connectivity

### Client Requirements
- Java Runtime Environment (JRE) 8 or higher
- Network connectivity to server
- Minimum 256 MB RAM
- Display resolution: 1024x768 or higher

## Installation and Setup

### Database Setup

1. **Install Oracle Database** (if not already installed)
2. **Create required tables**:
   ```sql
   -- Student table for authentication
   CREATE TABLE Student (
       ID NUMBER PRIMARY KEY,
       USERNAME VARCHAR2(50) NOT NULL,
       PASSWORD VARCHAR2(50) NOT NULL,
       MARKS NUMBER DEFAULT 0
   );
   
   -- Question table for examination content
   CREATE TABLE Question (
       ID NUMBER PRIMARY KEY,
       QUESTION_TEXT VARCHAR2(500) NOT NULL,
       OPTION1 VARCHAR2(200) NOT NULL,
       OPTION2 VARCHAR2(200) NOT NULL,
       OPTION3 VARCHAR2(200) NOT NULL,
       OPTION4 VARCHAR2(200) NOT NULL,
       CORRECT_ANSWER VARCHAR2(1) NOT NULL
   );
   ```

3. **Insert sample data**:
   ```sql
   -- Sample students
   INSERT INTO Student VALUES (1, 'student1', 'password123', 0);
   INSERT INTO Student VALUES (2, 'student2', 'password456', 0);
   
   -- Sample questions
   INSERT INTO Question VALUES (1, 'Which language is fully object-oriented?', 'C++', 'C', 'Java', 'Python', 'c');
   INSERT INTO Question VALUES (2, 'What is encapsulation in OOP?', 'Data hiding', 'Inheritance', 'Polymorphism', 'Abstraction', 'a');
   ```

### Server Setup

1. **Configure database connection**:
   - Edit `server/src/server/database.properties`
   - Update database credentials:
     ```properties
     db.driver=oracle.jdbc.driver.OracleDriver
     db.url=jdbc:oracle:thin:@localhost:1521:xe
     db.username=YOUR_USERNAME
     db.password=YOUR_PASSWORD
     server.port=5555
     ```

2. **Build and run server**:
   ```bash
   cd server
   ant clean compile
   ant run
   ```

### Client Setup

1. **Build and run client**:
   ```bash
   cd client
   ant clean compile
   ant run
   ```

2. **Connect to server**: The client will automatically attempt to connect to localhost:5555

## Usage Instructions

### For Administrators

1. **Start the server** application first
2. **Ensure database** is running and accessible
3. **Monitor server logs** for client connections and examination progress
4. **Manage questions and students** through database interface

### For Students

1. **Launch the client** application
2. **Enter credentials** on the login screen
3. **Answer questions** presented one by one
4. **Submit answers** by selecting options and clicking "Next"
5. **View results** after completing all questions

## Architecture Overview

```
┌─────────────┐    Network     ┌─────────────┐    JDBC    ┌─────────────┐
│   Client    │◄──────────────►│   Server    │◄──────────►│  Database   │
│   (GUI)     │   Socket       │ (Multi-     │            │  (Oracle)   │
│             │   Programming  │  threaded)  │            │             │
└─────────────┘                └─────────────┘            └─────────────┘
```

## Intended Users

This platform is suitable for:
- **Educational Institutions**: Universities, colleges, and schools
- **Training Centers**: Professional certification programs
- **Corporate Training**: Employee assessment and evaluation
- **Laboratory Examinations**: Computer-based practical tests

## Security Features

- ✅ **Input Validation**: Prevents malicious input
- ✅ **Prepared Statements**: Protection against SQL injection
- ✅ **External Configuration**: Database credentials not hardcoded
- ✅ **Resource Management**: Proper cleanup of connections and streams
- ✅ **Error Handling**: Comprehensive exception management

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Ensure server is running
   - Check firewall settings
   - Verify port 5555 is not blocked

2. **Database Connection Failed**
   - Verify Oracle database is running
   - Check credentials in database.properties
   - Ensure JDBC driver is in classpath

3. **Authentication Failed**
   - Verify username/password in database
   - Check Student table has correct data

## Future Enhancements

- 🔄 **Web-based Interface**: HTML/CSS frontend for better accessibility
- 🔄 **Question Bank Management**: Administrative interface for question management
- 🔄 **Load Balancer**: Support for high-concurrency scenarios
- 🔄 **Encryption**: Secure data transmission
- 🔄 **Reporting Module**: Detailed analytics and reports
- 🔄 **Timer Functionality**: Automatic exam completion
- 🔄 **Question Randomization**: Different question sets per student

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make improvements while maintaining backward compatibility
4. Test thoroughly
5. Submit a pull request

## License

This project is developed for educational purposes. Please refer to the license file for detailed terms.

## Support

For technical support or questions, please contact the development team or create an issue in the repository.
