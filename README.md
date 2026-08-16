# UniBox — Unified Complaint Management System

UniBox is a centralized complaint management system designed to simplify the process of submitting, tracking, managing, and resolving complaints through a structured backend application.

The project was developed using **Java and Spring Boot**, with **REST APIs, JWT authentication, role-based authorization, JPA/Hibernate, and PostgreSQL**.

## 🚀 Key Features

* User registration and authentication
* JWT-based authentication
* Role-based authorization
* Complaint creation and submission
* Complaint tracking and status management
* Department-based complaint handling
* Persistent data storage using PostgreSQL
* RESTful API architecture
* Layered application architecture

## 🛠️ Tech Stack

| Technology      | Usage                          |
| --------------- | ------------------------------ |
| Java            | Backend development            |
| Spring Boot     | Application framework          |
| Spring Security | Authentication & authorization |
| JWT             | Token-based authentication     |
| REST APIs       | Client-server communication    |
| JPA / Hibernate | ORM and database interaction   |
| PostgreSQL      | Database                       |
| Maven           | Dependency management & build  |
| Git             | Version control                |

## 🏗️ Architecture

UniBox follows a layered backend architecture:

```text
Client
   ↓
REST Controller
   ↓
Service Layer
   ↓
Repository Layer
   ↓
PostgreSQL Database
```

### Main Components

**Controller Layer**

* Handles HTTP requests and responses
* Exposes REST endpoints

**Service Layer**

* Contains business logic
* Processes complaint and user operations

**Repository Layer**

* Handles database operations using JPA/Hibernate

**Security Layer**

* Handles authentication
* Validates JWT tokens
* Applies role-based authorization

## 🔐 Authentication & Authorization

UniBox uses JWT-based authentication to secure backend APIs.

The authentication flow is:

```text
User Login
    ↓
Credentials Validation
    ↓
JWT Token Generated
    ↓
Token Sent with API Requests
    ↓
JWT Validation
    ↓
Role-Based Access
```

Different user roles can access functionality according to their permissions.

## 🗄️ Database

The application uses **PostgreSQL** for persistent data storage.

### Main Entities

```text
Users
Departments
Complaints
```

The database is mapped to Java entities using **JPA/Hibernate**.

## 🔌 REST API

Example endpoints include:

```text
POST   /api/auth/login
POST   /api/complaints
GET    /api/complaints
GET    /api/complaints/{id}
PUT    /api/complaints/{id}/status
```

> Endpoint names may vary depending on the current implementation.

## 🧪 API Testing

REST APIs were tested using **Postman** to verify request handling, authentication, authorization, status codes, and API responses.



## 📂 Project Structure

```text
src
├── main
│   ├── java
│   │   └── ...
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entity
│   │       ├── dto
│   │       ├── security
│   │       └── ...
│   │
│   └── resources
│       └── application.properties
│
└── test
```

## ⚙️ How to Run

### Prerequisites

Make sure you have:

* JDK 21 or compatible JDK
* Maven
* PostgreSQL
* Git

### 1. Clone the repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

### 2. Configure PostgreSQL

Create a PostgreSQL database and update the database configuration in:

```text
application.properties
```

Configure:

```properties
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### 3. Build the project

```bash
mvn clean install
```

### 4. Run the application

```bash
mvn spring-boot:run
```

The application will start on the configured Spring Boot port.

## 🎯 What I Learned

Through this project, I gained practical experience with:

* Developing RESTful APIs using Spring Boot
* Implementing JWT-based authentication
* Applying role-based authorization
* Designing relational database structures
* Working with JPA/Hibernate
* Structuring applications using Controller, Service, and Repository layers
* Testing APIs using Postman
* Managing source code using Git
* Applying software development practices including SDLC and Agile methodologies

## 🔮 Future Improvements

Potential improvements include:

* Email notifications for complaint updates
* File/image attachments for complaints
* Advanced complaint filtering and search
* Analytics dashboard
* Cloud deployment
* Automated testing
* Docker containerization

## 👨‍💻 Author

**Prashant Kumar Gautam**

Aspiring Java Backend Developer

**Core Technologies:** Java | Spring Boot | REST APIs | PostgreSQL | JPA/Hibernate | SQL | Git

---

⭐ If you find this project useful, feel free to explore the repository and provide feedback.
