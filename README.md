# Backend Service

This repository contains the backend service for the Taskaya freelancing platform. It is built using Spring Boot and Java, providing a robust and scalable foundation for the platform's operations.

## Features

This backend service includes functionalities for:

*   **User Management:** Handling user authentication, registration, and profiles.
*   **Freelancer and Client Management:** Specific services for managing freelancer and client data, including balances and business details.
*   **Work Management:** Core functionalities for job postings, proposals, contracts, milestones, and conversations related to work.
*   **Community Features:** Services for community posts, comments, and voting.
*   **Notifications and Mail:** System for sending notifications and emails.
*   **Payment Processing:** Integration for handling payments.
*   **File Storage:** Integration with Cloudinary for file uploads.
*   **Real-time Communication:** WebSocket support for real-time interactions.

## Technologies Used

The project leverages the following key technologies:

*   **Java 22:** The primary programming language.
*   **Spring Boot 3.4.1:** Framework for building the application.
*   **Maven:** Dependency management and build automation tool.
*   **MySQL:** Relational database for core application data.
*   **MongoDB:** NoSQL database, likely for specific data storage needs (e.g., real-time data, logs).
*   **Spring Security:** For authentication and authorization.
*   **JWT (JSON Web Tokens):** For secure API authentication.
*   **Lombok:** To reduce boilerplate code.
*   **Cloudinary:** For cloud-based image and video management.
*   **Spring WebSocket:** For real-time, two-way communication.
*   **H2 Database:** An in-memory database used for testing.

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

*   Java Development Kit (JDK) 22
*   Maven
*   MySQL Server
*   MongoDB Server

### Installation

1.  **Clone the repository:**

    ```bash
    git clone <repository_url>
    cd backend
    ```

2.  **Configure the database:**

    The application is configured to connect to a MySQL database named `taskaya_database`. Ensure your MySQL server is running and accessible. The `application.properties` file uses `spring.jpa.hibernate.ddl-auto=create-drop`, which will automatically create and drop the schema on application startup and shutdown. For production environments, consider a different `ddl-auto` strategy.

    You can find the database connection details in `src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/taskaya_database
    spring.datasource.username=root
    spring.datasource.password=root
    spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
    ```

    Similarly, configure your MongoDB connection:

    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/taskaya_database
    ```

3.  **Configure Cloudinary:**

    The application uses Cloudinary for file uploads. You will need to set up your Cloudinary account and configure the credentials in `application.properties`:

    ```properties
    cloudinary.cloud_name=your_cloud_name
    cloudinary.api_key=your_api_key
    cloudinary.api_secret=your_api_secret
    ```

    Replace `your_cloud_name`, `your_api_key`, and `your_api_secret` with your actual Cloudinary credentials.

4.  **Build the project:**

    Navigate to the root directory of the project (where `pom.xml` is located) and run:

    ```bash
    mvn clean install
    ```

### Running the Application

To run the Spring Boot application, use the following command from the project root:

```bash
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run it directly from your IDE (e.g., IntelliJ IDEA, Eclipse) as a Spring Boot application.
