<h1 align="center">🛒 Electronic Store Backend</h1>

<p align="center">
  <b>Monolithic Backend built with Spring Boot</b><br/>
  Spring Boot • Java • REST APIs • MySQL
</p>

<hr/>

<h2>📌 Project Overview</h2>

<p>
This project is a <b>monolithic backend application</b> for an <b>Electronic Store</b>.
It is designed using <b>clean layered architecture</b> and follows real-world
enterprise development practices.
</p>

<ul>
  <li>Architecture: <b>MVC (Monolithic)</b></li>
  <li>API Style: <b>REST</b></li>
  <li>Database: <b>MySQL</b></li>
</ul>

<hr/>

<h2>📦 Modules</h2>

<ul>
  <li>👤 User</li>
  <li>📦 Product</li>
  <li>📂 Category</li>
  <li>🛒 Cart</li>
  <li>📑 Order</li>
</ul>

<hr/>

<h2>✨ Features</h2>

<details>
  <summary><b>📄 Pagination & Sorting</b></summary>
  Efficient handling of large datasets using pageable APIs.
</details>

<details>
  <summary><b>✅ API Validation</b></summary>
  Bean validation for request data to ensure correctness.
</details>

<details>
  <summary><b>🧩 Custom Validation</b></summary>
  Custom validators for business-specific rules.
</details>

<details>
  <summary><b>📤 File Upload Service</b></summary>
  Upload and manage files (e.g., product images).
</details>

<details>
  <summary><b>🚨 Global Exception Handling</b></summary>
  Centralized exception handling with custom exceptions.
</details>

<details>
  <summary><b>🔐 Security (Spring Security 6.x)</b></summary>
  <ul>
    <li>Token-based Authentication (JWT)</li>
    <li>Role-based Authorization</li>
    <li>CORS & CSRF configuration</li>
    <li>Password Encoding</li>
    <li>Custom Security Filters</li>
    <li>Session Management</li>
  </ul>
</details>

<details>
  <summary><b>🔑 Login with Google</b></summary>
  OAuth2 based social login integration.
</details>

<details>
  <summary><b>🧪 Unit Testing</b></summary>
  Service layer testing using JUnit 5 & Mockito.
</details>

<details>
  <summary><b>📘 Swagger API Documentation</b></summary>
  Interactive API documentation using OpenAPI.
</details>

<details>
  <summary><b>🐳 Docker Support</b></summary>
  Dockerized application for easy deployment.
</details>

<details>
  <summary><b>🔢 Enums</b></summary>
  Used for fixed domain values (roles, status, etc.).
</details>

<hr/>

<h2>🏗️ Package Structure</h2>

<pre>
com.learn.electronicstore
├── config
├── controllers
├── dtos
├── entities
├── enums
├── exceptions
├── helper
├── repositories
├── security
├── services
├── validate
</pre>

<hr/>

<h2>🛠️ Tech Stack</h2>

<ul>
  <li>Java</li>
  <li>Spring Boot</li>
  <li>Spring Security 6.x</li>
  <li>Spring Data JPA</li>
  <li>MySQL</li>
  <li>JUnit 5 & Mockito</li>
  <li>Docker</li>
  <li>Git & GitHub</li>
  <li>Maven</li>
  <li>IntelliJ IDEA</li>
  <li>Postman</li>
</ul>

<hr/>

<h2>▶️ How to Run : Build and run JAR</h2>

<pre>
  mvn clean install
  java -jar target/electronic-store-backend.jar
</pre>

<hr/>

<h2>🎯 Interview Ready Talking Points</h2>

<ul>
  <li>Why monolithic architecture before microservices</li>
  <li>DTO vs Entity separation</li>
  <li>Global exception handling strategy</li>
  <li>Spring Security filter chain</li>
  <li>JWT authentication flow</li>
  <li>Validation & business rules</li>
</ul>

<hr/>

<h2>👨‍💻 Author</h2>

<p>
<b>Yogesh Bhagat</b><br/>
Software Engineer
</p>

