# Movie Ticket Booking System – Backend API

This is the backend service for the Movie Ticket Booking System, implemented in Java 17 using Spring Boot. It provides secure RESTful APIs for both user and admin interfaces to interact with features such as movie browsing, ticket booking, user authentication, seat reservation, and statistics.

---

## 1. Technology Stack

- Java 24+
- Spring Boot 3.5.3+
- Spring Security with JWT Authentication
- Spring Data JPA (MySQL)
- Stripe API (Payment Simulation)
- Cloudinary (Image Upload)
- SMTP Mail Service
- Maven

---

## 2. Getting Started

### Prerequisites

- Java 24+
- Maven 3+
- MySQL 8.x+
- Cloudinary, Stripe, and Mail accounts

### Setup Instructions

1. **Create `.env` file**

Create a `.env` file based on `.env.example`:

```bash
cp .env.example .env
```

2. **Start the server**

```bash
mvn spring-boot:run
```

By default, it runs at: `http://localhost:8080`

---

## 3. Project Structure

```
src/main/java/com/movie/main/
├── auth/            # Authentication logic
├── config/          # Swagger, Security, CORS
├── controller/      # REST Controllers
├── dto/             # Request and response DTOs
├── entity/          # JPA entities (User, Booking, Seat, etc.)
├── event/           # Domain events (e.g., SendEmailEvent)
├── exception/       # Global exception handlers
├── listener/        # Event listeners
├── repository/      # Spring Data JPA interfaces
├── resource/        # Default data loaders, constants
├── service/         # Business logic
├── ulti/            # Utility methods
└── MainApplication.java
```

---

## 4. API Overview

> For full documentation, open [Localhost Swagger UI](http://localhost:8080/swagger-ui/index.html)

---

## 5. Security & Authentication

- JWT-based Authentication with `Bearer` token
- Role-based Authorization (USER, ADMIN)
- Passwords are encrypted using BCrypt
- Secure exception handling and CORS settings

---

## 6. Environment Variables

This project uses a `.env` file to manage sensitive configuration for database access, JWT authentication, email services, image storage, and payment integration.

### Database Configuration

| Variable      | Description                                                                         |
| ------------- | ----------------------------------------------------------------------------------- |
| `DB_URL`      | JDBC connection URL to the MySQL database. Example: `jdbc:mysql://host:port/dbname` |
| `DB_USERNAME` | Username for the database                                                           |
| `DB_PASSWORD` | Password for the database user                                                      |

### JWT Configuration

| Variable      | Description                                                                        |
| ------------- | ---------------------------------------------------------------------------------- |
| `JWT_RAW_KEY` | Secret key used to sign and verify JWT tokens. Must be at least **32 characters**. |

### Default Admin

| Variable                 | Description                               |
| ------------------------ | ----------------------------------------- |
| `DEFAULT_ADMIN_USERNAME` | Default admin username (used for seeding) |
| `DEFAULT_ADMIN_PASSWORD` | Default admin password (used for seeding) |

> It's highly recommended to change these defaults in production environments.

### Email Configuration

| Variable        | Description                               |
| --------------- | ----------------------------------------- |
| `MAIL_HOST`     | SMTP server host (e.g., `smtp.gmail.com`) |
| `MAIL_PORT`     | SMTP port (`587` for TLS, `465` for SSL)  |
| `MAIL_USERNAME` | Email address used to send system emails  |
| `MAIL_PASSWORD` | App password or email service token       |

> For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your main password.

### Cloudinary Configuration (Image Hosting)

| Variable                | Description                |
| ----------------------- | -------------------------- |
| `CLOUDINARY_CLOUD_NAME` | Your Cloudinary cloud name |
| `CLOUDINARY_API_KEY`    | API key for Cloudinary     |
| `CLOUDINARY_API_SECRET` | API secret for Cloudinary  |

### Stripe Payment Integration

| Variable                | Description                                      |
| ----------------------- | ------------------------------------------------ |
| `STRIPE_SECRET_KEY`     | Secret key used for Stripe payments              |
| `STRIPE_WEBHOOK_SECRET` | Webhook secret to validate Stripe webhook events |
| `STRIPE_CURRENCY`       | Payment currency code (e.g., `vnd`, `usd`)       |

---

### Security Notice

- Do **not commit** your `.env` file to version control (check `.gitignore`).
- Use `.env.example` to share required environment variables without real credentials.
- For production, use secret managers (e.g., Docker secrets, Vault, AWS Secrets Manager).

---

### Example `.env` file

Refer to the `.env.example` file included in the project root to get started.

## 7. Production Recommendations

- Use Docker Secrets or secret managers (Vault, AWS Secrets Manager) in production
- Set `spring.jpa.hibernate.ddl-auto=validate` instead of `update`
- Host backend with reverse proxy (e.g., Nginx + HTTPS)
- Separate frontend and backend domains with CORS setup
