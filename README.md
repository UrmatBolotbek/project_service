# Project Service

**Helping startup creators to build great teams.**

Project Service is a core microservice within our integrated social network application. It enables users to create, manage, and collaborate on projects. In our platform, projects are central to organizing ideas, tasks, and resources—helping startup creators build great teams and grow their ventures.

---

## Overview

- **Project Management:**  
  Create and maintain projects with detailed attributes such as name, description, storage limits, cover images, and status. Projects support parent-child relationships to organize complex initiatives.

- **Collaboration and Integration:**  
  Integrated with other microservices (e.g., user_service, team management, post_service) to provide a seamless social networking experience where users can share updates, post content, and collaborate on projects.

- **Dynamic and Extensible:**  
  Designed to handle growing amounts of project data while remaining flexible enough to accommodate new features as the platform evolves.

---

## Data Model

The core `Project` entity includes, but is not limited to, the following fields:

- **ID, Name & Description:**  
  Unique identifier, project name (max 128 characters), and a detailed description (up to 4096 characters).

- **Storage Management:**  
  Tracks current and maximum storage size for project data.

- **Ownership and Hierarchy:**  
  Contains the owner ID, supports parent-child project relationships, and links to teams, tasks, and resources.

- **Timestamps and Status:**  
  Automatically managed creation and update timestamps, project status (e.g., ACTIVE, INACTIVE) and visibility settings (e.g., PUBLIC, PRIVATE).

- **Additional Associations:**  
  Supports associations with teams, schedules, stages, vacancies, moments, and meetings to facilitate comprehensive project management.

---

### Technologies Used

- [Spring Boot](https://spring.io/projects/spring-boot) – Main framework for building the application.
- [PostgreSQL](https://www.postgresql.org/) – Primary relational database.
- [Redis](https://redis.io/) – Used as a cache and for message queuing via pub/sub.
- [Testcontainers](https://testcontainers.com/) – For isolated testing with a real database.
- [Liquibase](https://www.liquibase.org/) – For managing database schema migrations.
- [Gradle](https://gradle.org/) – Build system.

### Database

- The PostgreSQL database is managed in a separate service ([infra](../infra)).
- Redis is deployed as a single instance in the [infra](../infra) service.
- Liquibase automatically applies the necessary migrations to a bare PostgreSQL instance at application startup.
- Integration tests use [Testcontainers](https://testcontainers.com/) to launch an isolated PostgreSQL instance.
- The code demonstrates data access using both JdbcTemplate and JPA (Hibernate).

### Conclusion

Project Service is an integral part of our social network platform, enabling startup creators to effectively manage and collaborate on projects. Its robust design and seamless integration with other microservices provide a powerful foundation for building great teams.

---
