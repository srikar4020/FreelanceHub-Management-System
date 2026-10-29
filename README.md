# FreelanceHub — Freelancing Management System

Beginner-friendly full-stack portfolio project using Java 17, Spring Boot, React, and MySQL.

## Workflows
- Admin: manage service categories and view users, services, and service requests.
- Freelancer: register/login, publish services, review requests, send a priced plan, revise rejected plans.
- Customer: register/login, browse services, request a service with requirements, accept/reject plans with a message.

## Requirements
Java 17+, Maven, Node.js 18+, MySQL 8+.

## Run
1. MySQL Workbench: `CREATE DATABASE freelancehub_management;`
2. Set your MySQL username/password in `backend/src/main/resources/application.properties`.
3. Terminal 1: `cd backend` then `mvn spring-boot:run`
4. Terminal 2: `cd frontend` then `npm install` and `npm run dev`
5. Open the Vite URL (normally http://localhost:5173).

## Demo admin account
Email: `admin@freelancehub.com`
Password: `Admin123!`

The demo admin is created on startup if it does not already exist. Change the demo password before any deployment.

## API outline
- POST `/api/auth/register`, POST `/api/auth/login`
- GET/POST/DELETE `/api/categories`
- GET/POST/DELETE `/api/services`
- GET/POST `/api/requests`
- GET `/api/requests/mine?userId=...`
- POST `/api/requests/{id}/plans`
- PUT `/api/plans/{id}/decision`

## Important security note
This is an educational demo, not production-ready authentication. It hashes passwords with BCrypt, but user IDs are passed by the client and the API does not use JWT/Spring Security authorization. Use it only locally for demonstration. Production needs authenticated sessions/JWT, server-side role authorization, tests, and secure configuration.

## Resume description
**FreelanceHub — Freelancing Management System** | Java, Spring Boot, React.js, MySQL, Spring Data JPA, REST APIs
- Developed a full-stack marketplace where freelancers publish categorized services and customers submit detailed service requests.
- Implemented a service-plan workflow that allows freelancers to propose prices and customers to accept or reject plans with feedback.
- Built REST APIs with Spring Boot and Spring Data JPA and connected them to a React interface with MySQL persistence.
- Created admin features to manage service categories and monitor users, service listings, and requests.
