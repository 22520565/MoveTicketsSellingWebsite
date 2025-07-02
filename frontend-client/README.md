# Movie Ticket Booking System – Frontend Client

This is the **customer-facing frontend** of the Movie Ticket Booking System, developed using **React + Vite** and styled with **Tailwind CSS**.

---

## Tech Stack

- **React.js** – Frontend library
- **Vite** – Build & Dev Server
- **Tailwind CSS** – Styling framework
- **React Router** – Client-side routing
- **Axios** – HTTP client for API communication
- **ESLint** – Linting tool for code quality

---

## Project Structure

```
frontend-client/
├── public/                  # Static assets
├── src/
│   ├── assets/              # Static files (images, fonts, etc.)
│   ├── Components/          # Reusable UI components
│   ├── config/              # Configuration constants (e.g. API base URL)
│   ├── Context/             # React Context for Auth, Cart, etc.
│   ├── layouts/             # Layout components (e.g. Header, Footer)
│   ├── pages/               # Route-based components (Home, MovieDetail, etc.)
│   ├── style/               # Tailwind/custom CSS
│   ├── utils/               # Helper functions
│   ├── App.css              # Global app styles
│   ├── index.css            # Tailwind + base styles
│   └── main.jsx             # Entry point for React app
├── db.json                  # Mock API data (optional for dev)
├── index.html               # HTML template
├── package.json             # Project dependencies and scripts
├── tailwind.config.cjs      # TailwindCSS configuration
├── vite.config.js           # Vite configuration
└── README.md                # Project documentation
```

---

## Getting Started

1. **Install dependencies:**

```bash
npm install
```

2. **Configure environment variables (if needed):**

> Example `.env`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

3. **Start the development server:**

```bash
npm run dev
```

4. **Build the project for production:**

```bash
npm run build
```

---

## Key Features

- Browse movies, showtimes, and book tickets
- Interactive seat selection UI
- Auth system with context
- Checkout and payment UI (Stripe-ready)
- Booking confirmation and ticket email simulation
