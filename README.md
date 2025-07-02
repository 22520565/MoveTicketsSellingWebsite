# Movie Ticket Booking System

A complete web application for online movie ticket booking. The system includes:

- **Frontend Client** – for end-users to browse, select, and book tickets
- **Frontend Admin** – for administrators to manage movies, cinemas, showtimes, and more
- **Backend API** – powered by Java Spring Boot, providing secure RESTful services

---

## Tech Stack Overview

| Layer             | Tech Used                                  |
| ----------------- | ------------------------------------------ |
| Frontend (Client) | React, Vite, Tailwind CSS, React Router    |
| Frontend (Admin)  | React, Vite, Tailwind CSS, Axios           |
| Backend           | Java 17, Spring Boot, Spring Security      |
| Database          | MySQL                                      |
| Others            | JWT Auth, Stripe (simulated), Docker-ready |

---

## Project Structure

```
MoveTicketsSellingWebsite-Dat/
├── backend/             # Java Spring Boot backend
├── frontend-client/     # Client-side React app
├── frontend-admin/      # Admin-side React app
```

---

## Getting Started

### 1. Backend Setup

```bash
cd backend
# Import into IntelliJ or VSCode with Spring support
# Create environment file `.env` and fill in the proper values, `.env.example` is provided for guidance.
# Run MainApplication.java to start the server
```

---

### 2. Frontend Client

```bash
cd frontend-client
npm install
npm run dev
```

Access via: [http://localhost:5173](http://localhost:5173)

---

### 3. Frontend Admin

```bash
cd frontend-admin
npm install
npm run dev
```

Access via: [http://localhost:5174](http://localhost:5174) (or whichever port Vite assigns)

---

## Features

### Client (User)

- Browse movies and showtimes
- Real-time seat selection
- Secure login & booking history
- Apply promotions and reward points
- Simulated Stripe checkout

### Admin

- Manage users, movies, and schedules
- Create promotions and discounts
- Assign movie-showtime-room
- View revenue and booking statistics

---

## Security

- JWT-based Authentication
- Role-based Authorization (Admin vs User)
- CORS & input validation

---

## Dev Notes

- Database schema is automatically created by Spring JPA (`ddl-auto=update`)
- Tailwind and ESLint preconfigured for both frontends
- Mock data can be stored in `db.json` during frontend dev
