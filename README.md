# 🧠 Serenity Mental Health Therapy Center Management System

A Java desktop application developed using **Hibernate ORM**, **JavaFX**, and **MySQL** to manage patients, therapists, therapy programs, appointments, payments, and system users for the Serenity Mental Health Therapy Center.

---

## 📖 Project Overview

The Serenity Mental Health Therapy Center Management System was developed to digitize and automate the center's patient registration and therapy management processes.

The system replaces traditional paper-based record keeping with a secure and efficient software solution that supports patient enrollment, therapy scheduling, therapist management, payment processing, reporting, and role-based access control.

---

## 🎯 Project Objectives

- Streamline patient registration procedures
- Manage therapy programs efficiently
- Handle therapist assignments and schedules
- Process payments and generate invoices
- Maintain secure user authentication
- Improve data accuracy and accessibility
- Generate reports and analytics for management

---

## ✨ Key Features

### 🔐 User Authentication & Authorization
- Secure login system
- BCrypt password encryption
- Role-based access control
- Admin and Receptionist user roles

### 👨‍⚕️ Therapist Management
- Add therapists
- Update therapist details
- Delete therapists
- Assign therapists to therapy programs
- Track therapist availability

### 📚 Therapy Program Management
- Create therapy programs
- Update program information
- Remove therapy programs
- Manage program fees and durations

### 👥 Patient Management
- Register new patients
- Update patient information
- Delete patient records
- View patient profiles
- Search and filter patients

### 📅 Therapy Session Scheduling
- Schedule therapy appointments
- Assign therapists
- Reschedule appointments
- Cancel appointments
- Prevent scheduling conflicts

### 💳 Payment & Invoice Management
- Process patient payments
- Generate invoices
- Track payment history
- Monitor pending payments

### 📊 Reporting & Analytics
- Therapy session reports
- Financial reports
- Patient history reports
- Therapist performance reports

---

## 🛠 Technologies Used

### Programming Language
- Java

### Frameworks
- Hibernate ORM
- JavaFX

### Database
- MySQL

### Security
- BCrypt Password Hashing

### Build Tool
- Maven

### IDE
- IntelliJ IDEA

---

## 🧩 Hibernate Features Implemented

- One-to-Many Relationships
- Many-to-One Relationships
- JPQL Queries
- HQL Queries
- Entity Mapping
- Lazy Loading
- Join Operations
- CRUD Operations

---

## 🔍 Advanced Functionalities

### HQL Join Query
Retrieve patients who are enrolled in all available therapy programs.

### Relationship Query
Fetch patients together with their enrolled therapy programs.

### BCrypt Encryption
Secure password storage and verification using BCrypt hashing.

### Custom Exception Handling
- Registration Errors
- Authentication Errors
- Payment Errors
- Scheduling Conflicts

### Validation
- Email Validation
- Phone Number Validation
- Required Field Validation
- Input Data Verification

---

## 📁 Project Structure

```text
src/
│
├── controller/
├── dao/
├── dto/
├── entity/
├── exception/
├── service/
├── util/
├── view/
│
├── resources/
│   ├── css/
│   ├── images/
│   └── fxml/
│
└── App.java
```

---

## 🚀 How to Run

### Clone Repository

```bash
git clone https://github.com/ANu-771/ITS1155-ORM-The-Serenity-Mental-Health-Therapy-Center.git
```

### Open Project

Open using IntelliJ IDEA.

### Configure Database

Update Hibernate configuration with your MySQL credentials.

### Install Dependencies

Reload Maven dependencies.

### Run Application

Run the main JavaFX application.

---

## 📚 Learning Outcomes

This project demonstrates:

- Object Relational Mapping (ORM)
- Hibernate Framework
- JavaFX UI Development
- Layered Architecture
- Secure Authentication
- Database Relationships
- Exception Handling
- Input Validation
- Software Engineering Best Practices

---

Developed by [ISURU ANUPAMA](https://github.com/ANu-771)

---

## 📄 License

This project was developed for educational purposes as part of the **ITS1155 ORM Coursework Assignment**.
