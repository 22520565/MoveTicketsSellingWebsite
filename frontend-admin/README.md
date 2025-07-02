# Movie Ticket Booking System – Frontend Admin

This is the **admin-side frontend** of the Movie Ticket Booking System. It provides tools for system administrators to manage users, movies, showtimes, cinema halls, promotions, and more.

---

## Tech Stack

- **React.js** – UI library
- **Vite** – Build tool & dev server
- **Tailwind CSS** – Styling framework
- **React Router** – Page navigation
- **Axios** – API communication
- **ESLint** – Code linting

---

## Project Structure

```
frontend-admin/
├── public/                    # Static assets (icons, SVGs, etc.)
├── src/
│   ├── assets/                # Images & media files
│   ├── components/            # Reusable components (Sidebar, Navbar, Table, etc.)
│   ├── config/                # Configuration constants (e.g. API base URL)
│   ├── contexts/              # Context providers (e.g. Auth, Global state)
│   ├── layouts/               # Layout wrappers
│   ├── pages/                 # Admin pages (Dashboard, Manage Users, etc.)
│   ├── ulitilities/           # Utility functions (typo: should be "utilities")
│   ├── index.css              # Tailwind and global styles
│   └── main.jsx               # Entry point for the app
├── index.html                 # HTML template
├── tailwind.config.js         # TailwindCSS configuration
├── vite.config.js             # Vite project configuration
└── package.json               # Dependencies and scripts
```

---

## Getting Started

1. **Install dependencies:**

```bash
npm install
```

2. **Optional: configure `.env`**

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

3. **Start development server:**

```bash
npm run dev
```

4. **Build for production:**

```bash
npm run build
```

---

## Key Features

- Manage users and staff
- Add/edit/delete movies and schedules
- Control cinema rooms, seats, and layouts
- View statistics and reports
- Create and manage promotions
- Intuitive sidebar navigation and dialogs
